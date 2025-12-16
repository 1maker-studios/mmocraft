package com.metype.mmocraft.skill;

import com.metype.mmocraft.MMOCraft;
import com.metype.mmocraft.interfaces.ICopyable;
import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.trait.ITrait;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SkillManager {
    private static final Map<Identifier, ISkill> skills = new HashMap<>();
    private static final Map<Identifier, List<ITrait>> traits = new HashMap<>();

    public static void registerSkill(ISkill skill) {
        if(skills.containsKey(skill.getID())) {
            MMOCraft.LOGGER.warn("Skill {} registered multiple times.", skill.getName());
        }
        skills.put(skill.getID(), skill);
    }

    @NotNull
    public static List<ISkill> getSkills() {
        return skills.values().stream().map(skill -> (ISkill) skill.copy()).toList();
    }

    public static void registerTrait(Identifier skillParent, ITrait trait) {
        List<ITrait> existing = traits.getOrDefault(skillParent, new ArrayList<>());
        existing.add(trait);
        traits.put(skillParent, existing);
    }

    @NotNull
    public static List<ITrait> getTraitsForSkill(Identifier skill) {
        if(!skills.containsKey(skill)) {
            MMOCraft.LOGGER.warn("Skill {} not registered, by was queried for traits.", skill);
            return List.of();
        }
        return traits.getOrDefault(skill, List.of()).stream().map(trait -> (ITrait) trait.copy()).toList();
    }

    public static int calculatePowerLevel(@NotNull MMOPlayer player) {
        int power = 0;
        for(ISkill skill : player.getSkills()) {
            power += skill.getLevel();
        }
        return power;
    }
}
