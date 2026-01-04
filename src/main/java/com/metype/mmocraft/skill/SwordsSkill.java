package com.metype.mmocraft.skill;

import com.metype.mmocraft.player.MMOPlayer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SwordsSkill extends Skill {
    public static Identifier ID = Identifier.of("mmocraft:swords");

    protected SwordsSkill(boolean flag) {
        super();
        name = "Swords";
        description = "Gain XP by attacking enemies with a sword. Passively increases sword attack damage.";
        id = ID;
        if(flag) {
            loadTraits();
        }
    }

    public static SwordsSkill defaultSkill() {
        return new SwordsSkill(false);
    }

    @Override
    public SwordsSkill copy() {
        return new SwordsSkill(true);
    }

    public static float playerSwordHandler(@NotNull MMOPlayer player, float damage) {
        SwordsSkill skill = (SwordsSkill) player.getSkill(ID);
        if(skill == null) return damage;

        skill.giveExperience((int) Math.floor(2 * damage));

        return damage * skill.swordDamageMultiplier();
    }

    private float swordDamageMultiplier() {
        return 1 + Math.min((level / 250.0f), 4); // Up to 5x
    }

    @Override
    public List<Passive> getPassives() {
        return List.of(
                new Passive("Damage Multiplier", "Multiplier for swords damage.", "{name} x{value}", swordDamageMultiplier())
        );
    }
}
