package com.metype.mmocraft.skill;

import com.metype.mmocraft.player.MMOPlayer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AxesSkill extends Skill {
    public static Identifier ID = Identifier.of("mmocraft:axes");

    protected AxesSkill(boolean flag) {
        super();
        name = "Axes";
        description = "Gain XP by attacking enemies with an axe. Passively increases axe attack damage.";
        id = ID;
        if(flag) {
            loadTraits();
        }
    }

    public static AxesSkill defaultSkill() {
        return new AxesSkill(false);
    }

    @Override
    public AxesSkill copy() {
        return new AxesSkill(true);
    }

    public static float playerAxeHandler(@NotNull MMOPlayer player, float damage) {
        AxesSkill skill = (AxesSkill) player.getSkill(ID);
        if(skill == null) return damage;

        skill.giveExperience((int) Math.floor(2 * damage));

        return damage * skill.axeDamageMultiplier();
    }

    private float axeDamageMultiplier() {
        return 1 + Math.min((level / 250.0f), 4); // Up to 5x
    }

    @Override
    public List<Passive> getPassives() {
        return List.of(
                new Passive("Damage Multiplier", "Multiplier for axe damage.", "{name} x{value}", axeDamageMultiplier())
        );
    }
}
