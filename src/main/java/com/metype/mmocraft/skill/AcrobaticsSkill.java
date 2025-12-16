package com.metype.mmocraft.skill;

import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.player.PlayerAdapter;
import com.metype.mmocraft.trait.ITrait;
import com.metype.mmocraft.util.UIUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AcrobaticsSkill implements ISkill {

    public static final Identifier ID = Identifier.of("mmocraft:acrobatics");;


    private int level = 0;
    private int xp = 0;
    private int sp = 0;
    private MMOPlayer player;

    private final Map<Identifier, ITrait> traits = new HashMap<>();

    public AcrobaticsSkill(boolean init) {
    }

    public AcrobaticsSkill() {
        for(ITrait trait : SkillManager.getTraitsForSkill(ID)) {
            traits.put(trait.getID(), trait);
            trait.associatePlayer(player);
        }
    }

    public AcrobaticsSkill(MMOPlayer associatedPlayer, int level, int xp, int sp) {
        this.level = level;
        this.xp = xp;
        this.sp = sp;
        this.player = associatedPlayer;
        for(ITrait trait : SkillManager.getTraitsForSkill(ID)) {
            traits.put(trait.getID(), trait);
            trait.associatePlayer(player);
        }
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public int getExperience() {
        return this.xp;
    }

    @Override
    public int getExperienceUntilNextLevel() {
        return (int) (100 + Math.pow(this.level, 2));
    }

    @Override
    public int getSkillPointsAvailable() {
        return this.sp;
    }

    @Override
    public void giveExperience(int amount) {
        Audience player = PlayerAdapter.getPlayer(this.player);
        xp += amount;
        while(xp >= getExperienceUntilNextLevel()) { // We don't want "100/100" XP, so >= lol
            xp -= getExperienceUntilNextLevel();
            level++;
            player.playSound(Sound.sound(SoundEvents.ENTITY_PLAYER_LEVELUP, Sound.Source.UI, 1.0f, 1.0f));
            UIUtil.showLevelUpMessage(this.player, this);
            if(level % 3 == 0) {
                sp++;
                UIUtil.showSkillPointGainMessage(this.player, this);
            }
        }
        UIUtil.showSkillBossBar(this.player, this);
    }

    @Override
    public void associatePlayer(MMOPlayer player) {
        this.player = player;
        for(ITrait trait: getTraits()) {
            trait.associatePlayer(player);
        }
    }

    @Override
    public void useSkillPoint() {
        this.sp--;
    }

    @Override
    public String getName() {
        return "Acrobatics";
    }

    @Override
    public Collection<ITrait> getTraits() {
        return traits.values().stream().toList();
    }

    @Override
    public ITrait getTrait(Identifier ID) {
        return traits.getOrDefault(ID, null);
    }

    @Override
    public AcrobaticsSkill copy() {
        return new AcrobaticsSkill();
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    public static float playerFallHandler(@NotNull MMOPlayer player, float fallDamage) {
        AcrobaticsSkill skill = (AcrobaticsSkill) player.getSkill(ID);
        if(skill == null) return fallDamage;

        skill.giveExperience((int) Math.floor(3 * fallDamage));

        player.save();

        return fallDamage * (1 - (Math.min(skill.level / 20.f, 50) / 100)); // Largest damage reduction is 50% at level 1000
    }

    @Override
    public String serialize() {
        return "acrobatics=" + level + "," + xp + "," + sp;
    }

    @Override
    public boolean deserialize(String data) {
        if(data == null) return false;
        String[] dataSplitOnEQ = data.split("=");
        if(dataSplitOnEQ.length < 2) return false;
        if(!dataSplitOnEQ[0].equalsIgnoreCase("acrobatics")) return false;
        String[] params = dataSplitOnEQ[1].split(",");
        if(params.length < 3) return false;
        level = Integer.parseInt(params[0]);
        xp = Integer.parseInt(params[1]);
        sp = Integer.parseInt(params[2].split("\\|")[0]);
        String[] traitData = data.split("\\|");
        for(ITrait trait : getTraits()) {
            for(String traitInfo: traitData) {
                if (trait.deserialize(traitInfo)) break;
            }
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "Gain XP by falling, gives passive fall damage reduction based on level.";
    }
}
