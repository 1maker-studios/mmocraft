package com.metype.mmocraft.command.trait;

import com.metype.mmocraft.command.ICommand;
import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.player.PlayerAdapter;
import com.metype.mmocraft.skill.Skill;
import com.metype.mmocraft.trait.Trait;
import com.metype.mmocraft.util.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TraitLevelUpCommand implements ICommand {
    @Override
    public LiteralArgumentBuilder<ServerCommandSource> build() {
        return literal("levelup")
                .then(argument("id", IdentifierArgumentType.identifier())
                        .executes(this::execute));
    }

    private int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if(!context.getSource().isExecutedByPlayer()) return CommandUtils.COMMAND_FAILURE;
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        MMOPlayer mmoPlayer = PlayerAdapter.getPlayer(player);
        Identifier ID = IdentifierArgumentType.getIdentifier(context, "id");
        for(Skill skill : mmoPlayer.getSkills()) {
            Trait trait = skill.getTrait(ID);
            if(trait == null) continue;
            if(trait.levelUp()) {
                player.playSoundToPlayer(SoundEvents.BLOCK_AMETHYST_BLOCK_STEP, SoundCategory.UI, 1, 1);
                context.getSource().getServer().getCommandManager().parseAndExecute(player.getCommandSource(), "/skill");
            }
        }
        return CommandUtils.COMMAND_SUCCESS;
    }
}
