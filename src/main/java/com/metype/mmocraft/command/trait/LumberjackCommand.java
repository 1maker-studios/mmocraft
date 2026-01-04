package com.metype.mmocraft.command.trait;

import com.metype.mmocraft.command.ICommand;
import com.metype.mmocraft.util.CommandUtils;
import com.metype.mmocraft.util.Utils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

import static net.minecraft.server.command.CommandManager.literal;

public class LumberjackCommand implements ICommand {

    public static final Map<UUID, Long> playersDesiringLumberjack = new HashMap<>();

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> build() {
        Utils.registerTickAction(server -> {
            if(server.getTicks() % 20 == 0) {
                List<UUID> toRemove = new ArrayList<>();
                for(UUID id : playersDesiringLumberjack.keySet()) {
                    if(System.currentTimeMillis() - playersDesiringLumberjack.get(id) > 30000) {
                        toRemove.add(id);
                        ServerPlayerEntity player = server.getPlayerManager().getPlayer(id);
                        if(player != null) {
                            player.sendMessage(Text.of("Lumberjack is no longer ready."));
                        }
                    }
                }
                for(UUID id : toRemove) {
                    playersDesiringLumberjack.remove(id);
                }
            }
            return true;
        });

        return literal("lumberjack")
                .requires(ServerCommandSource::isExecutedByPlayer)
                .executes(this::execute);
    }

    private int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        UUID id = context.getSource().getPlayerOrThrow().getUuid();
        playersDesiringLumberjack.put(id, System.currentTimeMillis());
        context.getSource().sendFeedback(() -> Text.of("Lumberjack ready..."), false);
        return CommandUtils.COMMAND_SUCCESS;
    }
}
