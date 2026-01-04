package com.metype.mmocraft.player;

import com.metype.mmocraft.MMOCraft;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class PlayerAdapter {
    private static final Map<UUID, MMOPlayer> cachedPlayers = new HashMap<>();

    public static MMOPlayer getPlayer(ServerPlayerEntity player) {
        MMOPlayer mmoPlayer = cachedPlayers.getOrDefault(player.getUuid(), new MMOPlayer(player));
        cachedPlayers.put(player.getUuid(), mmoPlayer);
        return mmoPlayer;
    }

    public static ServerPlayerEntity getPlayer(MMOPlayer player) {
        if(player == null) return null;
        return MMOCraft.SERVER.getPlayerManager().getPlayer(player.uuid);
    }

    public static void saveAllPlayers() {
        for(MMOPlayer player : cachedPlayers.values()) {
            player.save();
        }
    }
}
