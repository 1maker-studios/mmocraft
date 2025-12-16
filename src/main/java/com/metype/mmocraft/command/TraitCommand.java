package com.metype.mmocraft.command;

import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.player.PlayerAdapter;
import com.metype.mmocraft.skill.ISkill;
import com.metype.mmocraft.trait.ITrait;
import com.metype.mmocraft.util.CommandUtils;
import com.metype.mmocraft.util.UIUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TraitCommand implements ICommand {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        String[] aliases = new String[]{"trait"};

        for (String alias : aliases) {
            LiteralArgumentBuilder<ServerCommandSource> args = literal(alias)
                    .requires(cs -> Permissions.check(cs, "mmocraft.trait", 2))
                    .then(literal("levelup")
                            .then(argument("id", IdentifierArgumentType.identifier())
                            .executes(this::execute)));
            dispatcher.register(args);
        }
    }

    private int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if(!context.getSource().isExecutedByPlayer()) return CommandUtils.COMMAND_FAILURE;
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        MMOPlayer mmoPlayer = PlayerAdapter.getPlayer(player);
        Identifier ID = IdentifierArgumentType.getIdentifier(context, "id");
        for(ISkill skill : mmoPlayer.getSkills()) {
            ITrait trait = skill.getTrait(ID);
            if(trait == null) continue;
            if(trait.levelUp()) {
                player.playSoundToPlayer(SoundEvents.BLOCK_AMETHYST_BLOCK_STEP, SoundCategory.UI, 1, 1);
                mmoPlayer.save();
                context.getSource().getServer().getCommandManager().parseAndExecute(player.getCommandSource(), "/skill");
            }
        }
        return CommandUtils.COMMAND_SUCCESS;
    }
}
