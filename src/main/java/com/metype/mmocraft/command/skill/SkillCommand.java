package com.metype.mmocraft.command.skill;

import com.metype.mmocraft.command.ICommand;
import com.metype.mmocraft.command.provider.SkillIDProvider;
import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.player.PlayerAdapter;
import com.metype.mmocraft.skill.Passive;
import com.metype.mmocraft.skill.Skill;
import com.metype.mmocraft.trait.Trait;
import com.metype.mmocraft.util.CommandUtils;
import com.metype.mmocraft.util.UIUtil;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SkillCommand implements ICommand {

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> build() {
        return literal("skill")
                .requires(Permissions.require("mmocraft.skill", 0))
                .executes(this::execute)
                .then(argument("id", IdentifierArgumentType.identifier())
                        .suggests(SkillIDProvider.all())
                        .executes(context -> {
                            Identifier ID = IdentifierArgumentType.getIdentifier(context, "id");
                            return execute(context, ID);
                        }));
    }

    private int execute(CommandContext<ServerCommandSource> context) {
        return execute(context, null);
    }

    private int execute(CommandContext<ServerCommandSource> context, Identifier skillID) {
        if(!context.getSource().isExecutedByPlayer()) return CommandUtils.COMMAND_SUCCESS;
        ServerPlayerEntity executor = Objects.requireNonNull(context.getSource().getPlayer());
        MMOPlayer mmoPlayer = PlayerAdapter.getPlayer(executor);
        if(mmoPlayer == null) return CommandUtils.COMMAND_FAILURE;
        if(skillID == null) {
            drawPlayerSkillInfo(mmoPlayer);
        } else {
            Skill skill = mmoPlayer.getSkill(skillID);

            if(skill == null) {
                executor.sendMessage(Text.of("Skill ID not found."));
                return CommandUtils.COMMAND_FAILURE;
            }

            drawSkill(mmoPlayer, skill);
        }
        return CommandUtils.COMMAND_SUCCESS;
    }

    private void drawPlayerSkillInfo(@NotNull MMOPlayer player) {
        UIUtil.sendMessage(player,
                Component.text("--------------------").color(UIUtil.plainMessageColor)
                        .append(Component.text(" MMOCraft ").color(UIUtil.skillNameColor))
                        .append(Component.text("--------------------").color(UIUtil.plainMessageColor)
                        )
        );
        UIUtil.sendMessage(player, Component.text("Power Level: ").hoverEvent(Component.text("The combined level of every skill.")).color(UIUtil.skillNameColor)
                .append(Component.text(player.powerLevel()).color(UIUtil.intVariableMessageColor)));
        for(Skill skill : player.getSkills()) {
            UIUtil.sendMessage(player,
                    Component.text(skill.getName() + " ").color(UIUtil.skillNameColor).hoverEvent(Component.text(skill.getDescription().getString()))
                            .append(Component.text(skill.getLevel()).color(UIUtil.intVariableMessageColor))
                            .append(Component.text(" (").color(UIUtil.plainMessageColor))
                            .append(Component.text(skill.getExperience() + "/" + skill.getExperienceUntilNextLevel()).color(UIUtil.intVariableMessageColor))
                            .append(Component.text(" XP) (").color(UIUtil.plainMessageColor))
                            .append(Component.text(skill.getSkillPointsAvailable()).color(UIUtil.intVariableMessageColor))
                            .append(Component.text(" SP)").color(UIUtil.plainMessageColor))
            );
            for(Trait trait : skill.getTraits()) {
                UIUtil.sendMessage(player,
                    Component.text("      " + trait.getName() + " ").color(UIUtil.traitNameColor).hoverEvent(Component.text(trait.getDescription().getString()))
                            .append(Component.text(trait.getLevel()).color(UIUtil.intVariableMessageColor))
                            .append(Component.text(" [+]")
                                    .color(skill.getSkillPointsAvailable() > 0 ? UIUtil.interactableColor : UIUtil.disabledColor)
                                    .hoverEvent(Component.text("Spend a skill point in " + skill.getName() + " to level up this trait."))
                                    .clickEvent(ClickEvent.runCommand("/trait levelup " + trait.getID()))
                            )
                );
            }
        }
    }

    private void drawSkill(@NotNull MMOPlayer player, @NotNull Skill skill) {
        UIUtil.sendMessage(player,
                Component.text("--------------------").color(UIUtil.plainMessageColor)
                        .append(Component.text(" " + skill.getName() + " ").color(UIUtil.skillNameColor))
                        .append(Component.text("--------------------").color(UIUtil.plainMessageColor)
                        )
        );

        UIUtil.sendMessage(player,
                Component.text("Description: ").color(UIUtil.plainMessageColor)
                        .append(Component.text(skill.getDescription().getString()).color(UIUtil.plainMessageColor))
        );

        for(Passive passive : skill.getPassives()) {
            UIUtil.sendMessage(player,
                    Component.text("\n" + passive.display()).color(UIUtil.plainMessageColor)
                            .hoverEvent(Component.text(passive.description))
            );
        }

        if(!skill.getTraits().isEmpty()) {
            UIUtil.sendMessage(player,
                    Component.text("\nTraits: ").color(UIUtil.plainMessageColor)
            );

            for (Trait trait : skill.getTraits()) {
                UIUtil.sendMessage(player,
                        Component.text("  " + trait.getName() + " ").color(UIUtil.traitNameColor).hoverEvent(Component.text(trait.getDescription().getString()))
                                .append(Component.text(trait.getLevel()).color(UIUtil.intVariableMessageColor))
                                .append(Component.text(" [+]")
                                        .color(skill.getSkillPointsAvailable() > 0 ? UIUtil.interactableColor : UIUtil.disabledColor)
                                        .hoverEvent(Component.text("Spend a skill point in " + skill.getName() + " to level up this trait."))
                                        .clickEvent(ClickEvent.runCommand("/trait levelup " + trait.getID()))
                                )
                );
            }
        }
    }

}
