package com.metype.mmocraft.command.trait;

import com.metype.mmocraft.command.ICommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class TraitCommand implements ICommand {

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> build() {
        return literal("trait")
                .requires(Permissions.require("mmocraft.trait", 2))
                .then(new TraitLevelUpCommand().build());
    }

}
