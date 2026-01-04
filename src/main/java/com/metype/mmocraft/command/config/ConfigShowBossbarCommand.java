package com.metype.mmocraft.command.config;

import com.metype.mmocraft.MMOCraft;
import com.metype.mmocraft.command.ICommand;
import com.metype.mmocraft.config.MainConfig;
import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.player.PlayerAdapter;
import com.metype.mmocraft.util.CommandUtils;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ConfigShowBossbarCommand implements ICommand {
    @Override
    public LiteralArgumentBuilder<ServerCommandSource> build() {
        return literal("show_bossbar")
                .requires(ServerCommandSource::isExecutedByPlayer)
                .then(argument("show", BoolArgumentType.bool())
                        .executes(this::execute)
                );
    }

    private int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        MMOPlayer mmoPlayer = PlayerAdapter.getPlayer(player);
        mmoPlayer.setShowBossBar(BoolArgumentType.getBool(context, "show"));
        return CommandUtils.COMMAND_SUCCESS;
    }
}
