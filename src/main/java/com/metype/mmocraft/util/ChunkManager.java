package com.metype.mmocraft.util;

import com.metype.mmocraft.MMOCraft;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.commons.compress.compressors.gzip.GzipUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;

public class ChunkManager {

    Path worldDir;
    Path chunksDir;

    private static class ChunkInfo {
        byte[] blockData = new byte[12288];
        ChunkPos position;
        RegistryKey<World> world;
        AtomicBoolean loaded = new AtomicBoolean(false);

        private int blockDataIndex(BlockPos posInChunk) {
            return (posInChunk.getX() + (posInChunk.getY() * 16) + (posInChunk.getZ() * 16 * 384)) / 8;
        }

        private int byteIndex(BlockPos posInChunk) {
            return blockDataIndex(posInChunk) % 8;
        }

        public ChunkInfo(WorldChunk worldChunk) {
            this.position = worldChunk.getPos();
            this.world = worldChunk.getWorld().getRegistryKey();
        }

        public void setInvalid(BlockPos pos) {
            pos = pos.subtract(position.getStartPos());
            int idx = blockDataIndex(pos);
            if(idx < 0 || idx > blockData.length - 1) return;
            blockData[idx] |= (byte) (1 << byteIndex(pos));
        }

        public void setValid(BlockPos pos) {
            pos = pos.subtract(position.getStartPos());
            int idx = blockDataIndex(pos);
            if(idx < 0 || idx > blockData.length - 1) return;
            blockData[idx] &= (byte) ~(1 << byteIndex(pos));
        }

        public boolean isValid(BlockPos pos) {
            pos = pos.subtract(position.getStartPos());
            int idx = blockDataIndex(pos);
            if(idx < 0 || idx > blockData.length - 1) return false;
            return (blockData[idx] & (1 << byteIndex(pos))) == 0;
        }

        private File getChunkFile(Path parentDir) throws IOException {
            File file = new File(parentDir.toString() + "/" + world.getValue() + "/" + position.x + "," + position.z + ".mmo");
            if(!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            return file;
        }

        public void save(Path parentDir) {
            Thread.ofPlatform().name("MMOCraft Chunk Save").start(() -> {
                if(!loaded.get()) return;

                try {
                    File chunkFile = getChunkFile(parentDir);
                    if (!chunkFile.exists()) {
                        chunkFile.createNewFile();
                    }
                    try(OutputStream stream = new FileOutputStream(chunkFile)) {
                        try {
                            
                            stream.write(blockData);
                            stream.flush();
                            stream.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    MMOCraft.LOGGER.error("Error saving chunk data", e);
                }
            });
        }

        public void load(Path parentDir) {
            Thread.ofPlatform().name("MMOCraft Chunk Load").start(() -> {
                try {
                    File chunkFile = getChunkFile(parentDir);
                    if (!chunkFile.exists()) {
                        return;
                    }
                    byte[] data = Files.readAllBytes(chunkFile.toPath());
                    System.arraycopy(data, 0, blockData, 0, Math.min(data.length, blockData.length));
                    loaded.set(true);
                } catch (Exception e) {
                    MMOCraft.LOGGER.error("Error loading chunk data", e);
                }
            });
        }
    }

    private final Map<ChunkPos, Map<RegistryKey<World>, ChunkInfo>> chunks = new HashMap<>();

    public ChunkManager() {
        FabricLoader loader = FabricLoader.getInstance();
        worldDir = Path.of(loader.getGameDir() + "/world");
        chunksDir = Path.of(worldDir + "/mmocraft/");
        if (!new File(chunksDir.toString()).exists()) {
            File configFile = new File(chunksDir.toString());
            configFile.mkdirs();
        }
    }

    public void chunkLoad(ServerWorld world, WorldChunk chunk) {
        ChunkInfo thisChunk = new ChunkInfo(chunk);
        thisChunk.load(chunksDir);

        Map<RegistryKey<World>, ChunkInfo> possibleChunks = chunks.getOrDefault(chunk.getPos(), null);
        if(possibleChunks == null) {
            possibleChunks = new HashMap<>();
        }
        possibleChunks.put(world.getRegistryKey(), thisChunk);
        chunks.put(chunk.getPos(), possibleChunks);
    }

    public void chunkUnload(ServerWorld world, WorldChunk chunk) {
        Map<RegistryKey<World>, ChunkInfo> possibleChunks = chunks.getOrDefault(chunk.getPos(), null);
        if(possibleChunks == null) return;
        ChunkInfo thisChunk = possibleChunks.getOrDefault(world.getRegistryKey(), null);
        if(thisChunk == null) return;
        thisChunk.save(chunksDir);
        possibleChunks.remove(world.getRegistryKey());
        if(possibleChunks.isEmpty()) {
            chunks.remove(chunk.getPos());
        }
    }

    public void setValid(ServerWorld world, BlockPos pos) {
        Map<RegistryKey<World>, ChunkInfo> possibleChunks = chunks.getOrDefault(new ChunkPos(pos), null);
        if(possibleChunks == null || possibleChunks.isEmpty() || !possibleChunks.containsKey(world.getRegistryKey())) return;
        ChunkInfo thisChunk = possibleChunks.get(world.getRegistryKey());
        thisChunk.setValid(pos);
    }

    public void setInvalid(ServerWorld world, BlockPos pos) {
        Map<RegistryKey<World>, ChunkInfo> possibleChunks = chunks.getOrDefault(new ChunkPos(pos), null);
        if(possibleChunks == null || possibleChunks.isEmpty() || !possibleChunks.containsKey(world.getRegistryKey())) return;
        ChunkInfo thisChunk = possibleChunks.get(world.getRegistryKey());
        thisChunk.setInvalid(pos);
    }

    public boolean isInvalid(ServerWorld world, BlockPos pos) {
        Map<RegistryKey<World>, ChunkInfo> possibleChunks = chunks.getOrDefault(new ChunkPos(pos), null);
        if(possibleChunks == null || possibleChunks.isEmpty() || !possibleChunks.containsKey(world.getRegistryKey())) return true;
        ChunkInfo thisChunk = possibleChunks.get(world.getRegistryKey());
        return !thisChunk.isValid(pos);
    }
}
