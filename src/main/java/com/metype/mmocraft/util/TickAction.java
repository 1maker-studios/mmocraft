package com.metype.mmocraft.util;

import net.minecraft.server.MinecraftServer;

@FunctionalInterface
public interface TickAction {
    boolean execute(MinecraftServer server);
}