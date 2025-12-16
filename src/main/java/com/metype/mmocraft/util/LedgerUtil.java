package com.metype.mmocraft.util;

import com.github.quiltservertools.ledger.Ledger;
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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LedgerUtil {
    public static boolean getPlayerPlacedBlock(ServerWorld world, BlockPos pos) {
        // Create parameters
        ActionSearchParams.Builder params = new ActionSearchParams.Builder();
        Set<Negatable<Identifier>> worlds = new HashSet<>();
        worlds.add(Negatable.allow(world.getRegistryKey().getValue()));
        params.setWorlds(worlds);
        params.setActions(Set.of(Negatable.allow("block-place")));
        params.setBounds(BlockBox.create(pos, pos));
        // Run search
        CompletableFuture<SearchResults> future = Ledger.getApi().searchActions(params.build(), 0);
        // Blocks thread for result
        try {
            SearchResults results = future.get();
            return !results.getActions().isEmpty();
        } catch (InterruptedException | ExecutionException e) {
            MMOCraft.LOGGER.warn("Failed to query Ledger", e);
        }
        return false;
    }
}
