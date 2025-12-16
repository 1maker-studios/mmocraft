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
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.Style;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SkillCommand implements ICommand {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        String[] aliases = new String[]{"skill"};

        for(String alias : aliases) {
            LiteralArgumentBuilder<ServerCommandSource> args = literal(alias)
                    .requires(cs -> Permissions.check(cs, "mmocraft.skill", 2))
                    .executes(this::execute)
                    .then(argument("id", IdentifierArgumentType.identifier())
                            .executes(context -> {
                                Identifier ID = IdentifierArgumentType.getIdentifier(context, "id");
                                return execute(context, ID);
                            }));
            dispatcher.register(args);
        }
    }

    private int execute(CommandContext<ServerCommandSource> context) {
        return execute(context, null);
    }

    private int execute(CommandContext<ServerCommandSource> context, Identifier skillID) {
        if(!context.getSource().isExecutedByPlayer()) return CommandUtils.COMMAND_SUCCESS;
        ServerPlayerEntity executor = Objects.requireNonNull(context.getSource().getPlayer());
        MMOPlayer mmoPlayer = PlayerAdapter.getPlayer(executor);
        if(mmoPlayer == null) return CommandUtils.COMMAND_SUCCESS;
        if(skillID == null) {
            drawPlayerSkillInfo(mmoPlayer);
        }
        return CommandUtils.COMMAND_SUCCESS;
    }

    private void drawPlayerSkillInfo(@NotNull MMOPlayer player) {
        UIUtil.sendMessage(player,
                Component.text("-------------------- ").color(UIUtil.plainMessageColor)
                        .append(Component.text("MMOCraft").color(UIUtil.skillNameColor))
                        .append(Component.text(" --------------------").color(UIUtil.plainMessageColor)
                        )
        );
        for(ISkill skill : player.getSkills()) {
            UIUtil.sendMessage(player,
                    Component.text(skill.getName() + " ").color(UIUtil.skillNameColor).hoverEvent(Component.text(skill.getDescription()))
                            .append(Component.text(skill.getLevel()).color(UIUtil.intVariableMessageColor))
                            .append(Component.text(" (").color(UIUtil.plainMessageColor))
                            .append(Component.text(skill.getExperience() + "/" + skill.getExperienceUntilNextLevel()).color(UIUtil.intVariableMessageColor))
                            .append(Component.text(" XP) (").color(UIUtil.plainMessageColor))
                            .append(Component.text(skill.getSkillPointsAvailable()).color(UIUtil.intVariableMessageColor))
                            .append(Component.text(" SP)").color(UIUtil.plainMessageColor))
            );
            for(ITrait trait : skill.getTraits()) {
                UIUtil.sendMessage(player,
                    Component.text("      " + trait.getName() + " ").color(UIUtil.traitNameColor).hoverEvent(Component.text(trait.getDescription()))
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
