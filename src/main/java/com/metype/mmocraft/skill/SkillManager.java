package com.metype.mmocraft.skill;

import com.metype.mmocraft.MMOCraft;
import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.trait.Trait;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SkillManager {
    private static final Map<Identifier, Skill> skills = new HashMap<>();
    private static final Map<Identifier, List<Trait>> traits = new HashMap<>();

    public static void registerSkill(Skill skill) {
        if(skills.containsKey(skill.getID())) {
            MMOCraft.LOGGER.warn("Skill {} registered multiple times.", skill.getName());
        }
        skills.put(skill.getID(), skill);
    }

    @NotNull
    public static List<Skill> getSkills() {
        return skills.values().stream().map(skill -> (Skill) skill.copy()).toList();
    }

    public static void registerTrait(Identifier skillParent, Trait trait) {
        List<Trait> existing = traits.getOrDefault(skillParent, new ArrayList<>());
        existing.add(trait);
        traits.put(skillParent, existing);
    }

    @NotNull
    public static List<Trait> getTraitsForSkill(Identifier skill) {
        Skill parent = getSkill(skill);
        if(parent == null) return List.of();
        return traits.getOrDefault(skill, List.of()).stream().map(trait -> (Trait) trait.copy()).toList();
    }

    public static int calculatePowerLevel(@NotNull MMOPlayer player) {
        int power = 0;
        for(Skill skill : player.getSkills()) {
            power += skill.getLevel();
        }
        return power;
    }

    public static Skill getSkill(Identifier skill) {
        if(!skills.containsKey(skill)) {
            MMOCraft.LOGGER.warn("Skill {} not registered, by was queried.", skill);
            return null;
        }
        return skills.getOrDefault(skill, null);
    }
}
