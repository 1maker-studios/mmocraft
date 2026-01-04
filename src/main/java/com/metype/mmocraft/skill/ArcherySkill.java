package com.metype.mmocraft.skill;

import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.player.PlayerAdapter;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArcherySkill extends Skill {
    public static Identifier ID = Identifier.of("mmocraft:archery");

    protected ArcherySkill(boolean flag) {
        super();
        name = "Archery";
        description = "Gain XP by shooting enemies with a bow.";
        id = ID;
        if(flag) {
            loadTraits();
        }
    }

    public static ArcherySkill defaultSkill() {
        return new ArcherySkill(false);
    }

    @Override
    public ArcherySkill copy() {
        return new ArcherySkill(true);
    }

    public static float playerShootHandler(@NotNull MMOPlayer player, Entity victim, float damage) {
        ArcherySkill skill = (ArcherySkill) player.getSkill(ID);
        if(skill == null) return damage;

        double dist = Math.min(victim.getEntityPos().distanceTo(PlayerAdapter.getPlayer(player).getEntityPos()), 50);

        skill.giveExperience((int) Math.floor(100 * (damage / 10) * (dist / 50)));

        return damage * skill.archeryDamageMultiplier();
    }

    private float archeryDamageMultiplier() {
        return 1 + Math.min((level / 250.0f), 4); // Up to 5x
    }

    @Override
    public List<Passive> getPassives() {
        return List.of(
                new Passive("Damage Multiplier", "Multiplier for arrow damage.", "{name} x{value}", archeryDamageMultiplier())
        );
    }
}
