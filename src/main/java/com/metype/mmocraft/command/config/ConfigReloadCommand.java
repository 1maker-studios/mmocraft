package com.metype.mmocraft.command.config;

import com.metype.mmocraft.MMOCraft;
import com.metype.mmocraft.command.ICommand;
import com.metype.mmocraft.config.MainConfig;
import com.metype.mmocraft.util.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class ConfigReloadCommand implements ICommand {
    @Override
    public LiteralArgumentBuilder<ServerCommandSource> build() {
        return literal("reload")
                .requires(Permissions.require("mmocraft.config.reload", 2))
                .executes(this::execute);
    }

    private int execute(CommandContext<ServerCommandSource> context) {
        MMOCraft.MAIN_CONFIG = MainConfig.load();
        context.getSource().sendFeedback(() -> Text.of("Reloaded config!"), true);
        return CommandUtils.COMMAND_SUCCESS;
    }
}
