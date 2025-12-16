package com.metype.mmocraft.util;

import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.player.PlayerAdapter;
import com.metype.mmocraft.skill.ISkill;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UIUtil {

    public static TextColor disabledColor = TextColor.fromHexString("#626262");
    public static TextColor interactableColor = TextColor.fromHexString("#733AF7");
    public static TextColor plainMessageColor = TextColor.fromHexString("#85D856");
    public static TextColor intVariableMessageColor = TextColor.fromHexString("#EFDA17");
    public static TextColor skillNameColor = TextColor.fromHexString("#6377D6");
    public static TextColor traitNameColor = TextColor.fromHexString("#E065CC");

    private static class PlayerWithBossBarForSkill {
        public int timer;
        public Identifier skill;
        public BossBar bossBar;
        public Audience viewer;
    }

    private static final List<PlayerWithBossBarForSkill> bossBarViewers = new ArrayList<>();

    public static void showTraitAction(MMOPlayer player, String message) {
        Audience playerAudience = PlayerAdapter.getPlayer(player);
        playerAudience.sendActionBar(Component.text(message));
    }

    public static void showLevelUpMessage(MMOPlayer player, ISkill skill) {
        Audience playerAudience = PlayerAdapter.getPlayer(player);

        playerAudience.sendMessage(
                Component.text("You are now level ").color(plainMessageColor)
                        .append(Component.text(skill.getLevel()).color(intVariableMessageColor).decorate(TextDecoration.BOLD))
                        .append(Component.text(" in ").color(plainMessageColor))
                        .append(Component.text(skill.getName()).color(skillNameColor))
        );
    }

    public static void showSkillPointGainMessage(MMOPlayer player, ISkill skill) {
        Audience playerAudience = PlayerAdapter.getPlayer(player);

        playerAudience.sendMessage(
                Component.text("+1 ").color(intVariableMessageColor)
                        .append(Component.text(skill.getName()).color(skillNameColor))
                        .append(Component.text(" Skill Point").color(plainMessageColor))
        );
    }

    private static BossBar getXPBarForSkill(ISkill skill) {
        return BossBar.bossBar(
                Component.text(skill.getName() + " ").color(skillNameColor)
                        .append(Component.text(skill.getLevel() + " ").color(intVariableMessageColor))
                        .append(Component.text("(").color(plainMessageColor))
                        .append(Component.text(skill.getExperience()).append(Component.text("/").append(Component.text(skill.getExperienceUntilNextLevel() + " XP"))).color(intVariableMessageColor))
                        .append(Component.text(")").color(plainMessageColor)),
                (float) skill.getExperience() / skill.getExperienceUntilNextLevel(),
                        BossBar.Color.BLUE,
                        BossBar.Overlay.PROGRESS
        );
    }

    public static void showSkillBossBar(MMOPlayer player, ISkill skill) {
        Audience audiencePlayer = PlayerAdapter.getPlayer(player);

        PlayerWithBossBarForSkill v = null;

        for(PlayerWithBossBarForSkill p : bossBarViewers) {
            if(p.skill.compareTo(skill.getID()) == 0) {
                p.viewer.hideBossBar(p.bossBar);
                v = p;
                break;
            }
        }

        if(v != null) {
            bossBarViewers.remove(v);
        }

        BossBar bar = getXPBarForSkill(skill);

        PlayerWithBossBarForSkill p = new PlayerWithBossBarForSkill();
        p.timer = 100;
        p.skill = skill.getID();
        p.bossBar = bar;
        p.viewer = audiencePlayer;

        bossBarViewers.add(p);
        audiencePlayer.showBossBar(p.bossBar);
    }

    public static void tickUiElements() {
        List<PlayerWithBossBarForSkill> toRemove = new ArrayList<>();

        for(PlayerWithBossBarForSkill p : bossBarViewers) {
            p.timer--;
            if(p.timer <= 0) {
                p.viewer.hideBossBar(p.bossBar);
                toRemove.add(p);
            }
        }

        bossBarViewers.removeAll(toRemove);
    }

    public static void sendMessage(@NotNull MMOPlayer player, ComponentLike message) {
        Audience audiencePlayer = PlayerAdapter.getPlayer(player);
        audiencePlayer.sendMessage(message);
    }
}
