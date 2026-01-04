package com.metype.mmocraft.util;

import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Objects;

public class Utils {
    private static ArrayList<TickAction> tickActions = new ArrayList<>();
    public static void tick(MinecraftServer server) {
        tickActions = new ArrayList<>(tickActions.stream().map(tickAction -> {
            if(!tickAction.execute(server)) {
                return null;
            }
            return tickAction;
        }).filter(Objects::nonNull).toList());
    }

    public static void registerTickAction(TickAction action) {
        tickActions.add(action);
    }
}
