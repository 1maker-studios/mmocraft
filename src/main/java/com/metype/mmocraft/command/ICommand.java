package com.metype.mmocraft.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

public interface ICommand {
    void register(CommandDispatcher<ServerCommandSource> dispatcher);
}
