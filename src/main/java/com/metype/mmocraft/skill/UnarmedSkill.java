package com.metype.mmocraft.skill;

import com.metype.mmocraft.player.MMOPlayer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UnarmedSkill extends Skill {
    public static Identifier ID = Identifier.of("mmocraft:unarmed");

    protected UnarmedSkill(boolean flag) {
        super();
        name = "Unarmed";
        description = "Gain XP by attacking enemies without a weapon. Passively increases unarmed attack damage.";
        id = ID;
        if(flag) {
            loadTraits();
        }
    }

    public static UnarmedSkill defaultSkill() {
        return new UnarmedSkill(false);
    }

    @Override
    public UnarmedSkill copy() {
        return new UnarmedSkill(true);
    }

    public static float playerUnarmedHandler(@NotNull MMOPlayer player, float damage) {
        UnarmedSkill skill = (UnarmedSkill) player.getSkill(ID);
        if(skill == null) return damage;

        skill.giveExperience((int) Math.floor(5 * damage));

        return damage * skill.unarmedDamageMultiplier();
    }

    private float unarmedDamageMultiplier() {
        return 1 + Math.min((level / 250.0f), 4); // Up to 5x
    }

    @Override
    public List<Passive> getPassives() {
        return List.of(
                new Passive("Damage Multiplier", "Multiplier for unarmed damage.", "{name} x{value}", unarmedDamageMultiplier())
        );
    }
}
