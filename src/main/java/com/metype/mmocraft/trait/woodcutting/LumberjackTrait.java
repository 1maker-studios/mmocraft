package com.metype.mmocraft.trait.woodcutting;

import com.metype.mmocraft.MMOCraft;
import com.metype.mmocraft.player.PlayerAdapter;
import com.metype.mmocraft.trait.Trait;
import com.metype.mmocraft.util.CommandUtils;
import com.metype.mmocraft.util.Utils;
import net.kyori.adventure.text.Component;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;

public class LumberjackTrait extends Trait {
    public static Identifier ID = Identifier.of("mmocraft:lumberjack");

    private int endTick = 0;
    private int nextAvailable = 0;

    public LumberjackTrait() {
        this.name = "Lumberjack";
    }

    private static final Map<Block, List<Block>> treeBlocks = Map.of(
            Blocks.OAK_LOG, List.of(Blocks.OAK_LEAVES, Blocks.VINE),
            Blocks.BIRCH_LOG, List.of(Blocks.BIRCH_LEAVES),
            Blocks.SPRUCE_LOG, List.of(Blocks.SPRUCE_LEAVES),
            Blocks.DARK_OAK_LOG, List.of(Blocks.DARK_OAK_LEAVES),
            Blocks.PALE_OAK_LOG, List.of(Blocks.PALE_OAK_LEAVES, Blocks.CREAKING_HEART),
            Blocks.JUNGLE_LOG, List.of(Blocks.JUNGLE_LEAVES, Blocks.COCOA, Blocks.VINE),
            Blocks.ACACIA_LOG, List.of(Blocks.ACACIA_LEAVES),
            Blocks.CHERRY_LOG, List.of(Blocks.CHERRY_LEAVES),
            Blocks.MANGROVE_LOG, List.of(Blocks.MANGROVE_LEAVES, Blocks.MANGROVE_ROOTS, Blocks.MANGROVE_PROPAGULE, Blocks.VINE),
            Blocks.MANGROVE_ROOTS, List.of(Blocks.MANGROVE_LEAVES, Blocks.MANGROVE_LOG, Blocks.MANGROVE_PROPAGULE, Blocks.VINE)
    );

    private static final int maxLumberjackBlockDestroy = 1024;

    public void playerBrokeLog(ServerWorld world, BlockPos position, BlockState state) {
        if(endTick <= world.getServer().getTicks()) {
            return;
        }
        if(!treeBlocks.containsKey(state.getBlock())) return;

        List<Block> validBlocks = new ArrayList<>(treeBlocks.get(state.getBlock()));
        validBlocks.add(state.getBlock());

        Random rng = new Random();
        rng.setSeed(System.currentTimeMillis());

        for(BlockPos blockPos : floodFill(world, position, validBlocks)) {
            world.breakBlock(blockPos, true);
            world.updateNeighbors(blockPos, Blocks.AIR);

            parent.giveExperience(rng.nextFloat() > 0.75 ? 1 : 0); // Let's be generous, but not super generous lol :3
        }
    }

    protected boolean isPartOfTree(BlockPos pos, BlockPos root) {
        return Math.abs(pos.getX() - root.getX()) < 10 &
                Math.abs(pos.getZ() - root.getZ()) < 10;
    }

    protected List<BlockPos> floodFill(ServerWorld world, BlockPos position, List<Block> allowedBlocks) {
        Deque<BlockPos> stack = new ArrayDeque<>();
        Set<BlockPos> set = new HashSet<>();
        stack.push(position);

        while(!stack.isEmpty() && set.size() < maxLumberjackBlockDestroy)
        {
            BlockPos blockPos = stack.pop();

            if(set.contains(blockPos))
                continue;

            if((allowedBlocks.contains(world.getBlockState(blockPos).getBlock()) || blockPos.equals(position)) && isPartOfTree(blockPos, position))
            {
                set.add(blockPos);

                for(Direction direction : Direction.values())
                {
                    BlockPos offset = blockPos.offset(direction);

                    if(!MMOCraft.CHUNK_MANAGER.isInvalid(world, offset)) stack.push(offset);
                }
            }
        }

        // if tree too big, don't try to tree the tree
        if(set.size() >= maxLumberjackBlockDestroy) {
            ServerPlayerEntity player = PlayerAdapter.getPlayer(this.player);
            if(player != null) player.sendMessage(Text.of("Tree is too large!"));
            return List.of();
        }

        return set.stream().toList();
    }

    private void showAbilityActionbar(MinecraftServer server, ServerPlayerEntity playerEntity) {
        if(playerEntity == null) return;

        playerEntity.sendActionBar(
                Component.text(
                        CommandUtils.format(
                                "Lumberjack Active! ({length}s)",
                                Map.of("length", (endTick - server.getTicks()) / 20)
                        )
                )
        );
    }

    public boolean tryUseAbility(MinecraftServer serverRef) {
        if(nextAvailable > serverRef.getTicks()) {
            return false;
        }
        int startTick = serverRef.getTicks();
        endTick = startTick + calculateLength();
        nextAvailable = (5 * 60 * 20) + endTick;

        ServerPlayerEntity player = PlayerAdapter.getPlayer(this.player);

        Utils.registerTickAction(server -> {
            showAbilityActionbar(server, player);
            return server.getTicks() < endTick;
        });

        return true;
    }

    public int getNextAvailable() {
        return nextAvailable;
    }

    public int calculateLength() {
        return 200 + (level * 60); // 3 seconds per level seems fair?
    }

    @Override
    public double modulate(double input, double factor) {
        // Linearly scale roll chance between 0.0 - 0.5 based on trait level, capping at level 100
        // Incorporate factor here just in case down-the-line something like luck or whatever is factored in
        return (Math.min(level, 100.f) / 200) * factor;
    }

    @Override
    public LumberjackTrait copy() {
        return new LumberjackTrait();
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public String getActionMessage() {
        return CommandUtils.format("Lumberjack ({timer})", Map.of("timer", 0));
    }

    @Override
    public Text getDescription() {
        return Text.of("Gives you a period of time, based on level, where chopping a tree's log fells the whole tree in one go.");
    }
}
