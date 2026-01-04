package com.metype.mmocraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public interface ICommand {
    LiteralArgumentBuilder<ServerCommandSource> build();
}
