package com.metype.mmocraft.skill;

import com.metype.mmocraft.interfaces.*;
import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.trait.ITrait;
import net.minecraft.util.Identifier;

import java.util.Collection;

public interface ISkill extends INamed, ISerializable, ICopyable, IIdentifiable, IDescribable {
    int getLevel();
    int getExperience();
    int getExperienceUntilNextLevel();
    int getSkillPointsAvailable();
    void giveExperience(int amount);
    void associatePlayer(MMOPlayer player);
    void useSkillPoint();

    Collection<ITrait> getTraits();
    ITrait getTrait(Identifier ID);
}
