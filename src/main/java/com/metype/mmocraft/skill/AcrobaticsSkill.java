package com.metype.mmocraft.skill;

import com.metype.mmocraft.player.MMOPlayer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AcrobaticsSkill extends Skill {
    public static Identifier ID = Identifier.of("mmocraft:acrobatics");

    protected AcrobaticsSkill(boolean flag) {
        super();
        name = "Acrobatics";
        description = "Gain XP by falling, gives passive fall damage reduction based on level.";
        id = ID;
        if(flag) {
            loadTraits();
        }
    }

    public static AcrobaticsSkill defaultSkill() {
        return new AcrobaticsSkill(false);
    }

    @Override
    public AcrobaticsSkill copy() {
        return new AcrobaticsSkill(true);
    }

    public static float playerFallHandler(@NotNull MMOPlayer player, float fallDamage) {
        AcrobaticsSkill skill = (AcrobaticsSkill) player.getSkill(ID);
        if(skill == null) return fallDamage;

        skill.giveExperience((int) Math.floor(3 * fallDamage));

        return fallDamage * skill.fallDamageReductionPercentage(); // Largest damage reduction is 50% at level 1000
    }

    private float fallDamageReductionPercentage() {
        return (1 - (Math.min(level / 20.f, 50) / 100));
    }

    @Override
    public List<Passive> getPassives() {
        return List.of(
                new Passive("Fall Reduction", "Reduces amount of fall damage taken", "{name} {value}%", 100 - (int)(fallDamageReductionPercentage() * 100))
        );
    }
}
