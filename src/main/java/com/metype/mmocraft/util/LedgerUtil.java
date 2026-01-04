package com.metype.mmocraft.util;

import com.github.quiltservertools.ledger.Ledger;
import com.github.quiltservertools.ledger.actions.ActionType;
import com.github.quiltservertools.ledger.actions.BlockBreakActionType;
import com.github.quiltservertools.ledger.actions.BlockPlaceActionType;
import com.github.quiltservertools.ledger.actionutils.ActionFactory;
import com.github.quiltservertools.ledger.actionutils.ActionSearchParams;
import com.github.quiltservertools.ledger.actionutils.SearchResults;
import com.github.quiltservertools.ledger.utility.Negatable;
import com.metype.mmocraft.MMOCraft;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class LedgerUtil {
//    public static boolean getPlayerPlacedBlock(ServerWorld world, BlockPos pos) {
//        // Create parameters
//        ActionSearchParams.Builder params = new ActionSearchParams.Builder();
//        Set<Negatable<Identifier>> worlds = new HashSet<>();
//        worlds.add(Negatable.allow(world.getRegistryKey().getValue()));
//        params.setWorlds(worlds);
//        params.setActions(Set.of(Negatable.allow("block-place")));
//        params.setBounds(BlockBox.create(pos, pos));
//        // Run search
//        CompletableFuture<SearchResults> future = Ledger.getApi().searchActions(params.build(), 0);
//        // Blocks thread for result
//        try {
//            SearchResults results = future.get();
//            return !results.getActions().isEmpty();
//        } catch (InterruptedException | ExecutionException e) {
//            MMOCraft.LOGGER.warn("Failed to query Ledger", e);
//        }
//        return false;
//    }
//
//    private static final ActionSearchParams.Builder params = new ActionSearchParams.Builder();
//
//    public static Map<BlockPos, Boolean> getBlocksCanBeSafelyAssumedToBeNatural(ServerWorld world, BlockBox box, List<BlockPos> allCaredAbout) {
//        params.setWorlds(Set.of(Negatable.allow(world.getRegistryKey().getValue())));
//        params.setBounds(box);
//        params.setActions(Set.of(Negatable.allow("block-place"), Negatable.allow("block-break")));
//
//        int totalPages = 1;
//        int currentPage = 0;
//
//        Map<BlockPos, Boolean> blocks = new HashMap<>();
//        Map<BlockPos, Instant> blockTimes = new HashMap<>();
//
//        List<BlockPos> blockList = new ArrayList<>(List.of(allCaredAbout.toArray(BlockPos[]::new)));
//
//        while(totalPages > currentPage && !blockList.isEmpty()) {
//            CompletableFuture<SearchResults> future = Ledger.getApi().searchActions(params.build(), currentPage++);
//            try {
//                SearchResults results = future.get();
//                totalPages = results.getPages();
//                for (ActionType action : results.getActions()) {
//
//                    BlockPos position = action.getPos();
//
//                    if (blockTimes.getOrDefault(position, Instant.MIN).isAfter(action.getTimestamp())) continue;
//                    blockTimes.put(position, action.getTimestamp());
//                    blockList.remove(position);
//
//                    if (action.getIdentifier().equalsIgnoreCase("block-place")) {
//                        blocks.put(position, false);
//                    }
//
//                    if (action.getIdentifier().equalsIgnoreCase("block-break")) {
//                        blocks.put(position, true);
//                    }
//                }
//            } catch (InterruptedException | ExecutionException e) {
//                MMOCraft.LOGGER.warn("Failed to query Ledger", e);
//                break;
//            }
//        }
//        return blocks;
//    }
//
//    public static boolean getBlockCanBeSafelyAssumedToBeNatural(ServerWorld world, BlockPos pos) {
//        return getBlocksCanBeSafelyAssumedToBeNatural(world, BlockBox.create(pos, pos), List.of(pos)).getOrDefault(pos, true);
//    }
}
