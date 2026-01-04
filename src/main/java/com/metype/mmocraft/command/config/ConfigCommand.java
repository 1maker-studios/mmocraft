package com.metype.mmocraft.command.config;

import com.metype.mmocraft.command.ICommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ConfigCommand implements ICommand {
    @Override
    public LiteralArgumentBuilder<ServerCommandSource> build() {
        return literal("mmo")
                .requires(Permissions.require("mmocraft.config", 0))
                .then(new ConfigShowBossbarCommand().build())
                .then(new ConfigReloadCommand().build());
    }
}
