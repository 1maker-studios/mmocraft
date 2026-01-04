package com.metype.mmocraft.skill;

import com.metype.mmocraft.config.ConfigUtils;
import com.metype.mmocraft.interfaces.*;
import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.player.PlayerAdapter;
import com.metype.mmocraft.trait.Trait;
import com.metype.mmocraft.util.UIUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Skill implements INamed, ISerializable, ICopyable, IIdentifiable, IDescribable {
    protected int xp;
    protected int level;
    protected int sp;
    protected String name;
    protected String description;
    protected Identifier id;

    public static Identifier ID = Identifier.of("mmocraft:skill");

    protected MMOPlayer player;

    protected final Map<Identifier, Trait> traits = new HashMap<>();

    protected Skill(boolean flag) { }

    public static Skill defaultSkill() {
        return new Skill(false);
    }

    public Skill() {
        name = "Skill";
        description = "Default skill";
        id = ID;
    }

    protected void loadTraits() {
        for(Trait trait : SkillManager.getTraitsForSkill(getID())) {
            traits.put(trait.getID(), trait);
            trait.associatePlayer(player);
            trait.setParent(this);
        }
    }

    public List<Passive> getPassives() {
        return List.of();
    }

    public int getLevel() { return level; }

    public int getExperience() { return xp; }

    public int getExperienceUntilNextLevel() { return (int) (100 + Math.pow(this.level, 2)); }

    public int getSkillPointsAvailable() { return sp; }

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
                player.playSound(Sound.sound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, Sound.Source.UI, 0.25f, 1.0f));
                UIUtil.showSkillPointGainMessage(this.player, this);
            }
            ConfigUtils.checkRunLevelUpCommand(this.player, this.player.powerLevel());
        }
        UIUtil.showSkillBossBar(this.player, this);
    }

    public void associatePlayer(MMOPlayer player) {
        this.player = player;
        for(Trait trait : getTraits()) {
            trait.associatePlayer(player);
        }
    }

    public void useSkillPoint() {
        this.sp--;
    }

    public Collection<Trait> getTraits() {
        return traits.values().stream().toList();
    }

    public Trait getTrait(Identifier ID) {
        return traits.getOrDefault(ID, null);
    }

    @Override
    public ICopyable copy() {
        return new Skill();
    }

    @Override
    public Text getDescription() {
        return MutableText.of(PlainTextContent.of(description)).setStyle(Style.EMPTY.withColor(UIUtil.plainMessageColor.value()));
    }

    @Override
    public Identifier getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String serialize() {
        return getName().toLowerCase() + "=" + level + "," + xp + "," + sp;
    }

    @Override
    public boolean deserialize(String data) {
        if(data == null) return false;
        String[] dataSplitOnEQ = data.split("=");
        if(dataSplitOnEQ.length < 2) return false;
        if(!dataSplitOnEQ[0].equalsIgnoreCase(getName())) return false;
        String[] params = dataSplitOnEQ[1].split(",");
        if(params.length < 3) return false;
        level = Integer.parseInt(params[0]);
        xp = Integer.parseInt(params[1]);
        sp = Integer.parseInt(params[2].split("\\|")[0]);
        String[] traitData = data.split("\\|");
        for(Trait trait : getTraits()) {
            for(String traitInfo: traitData) {
                if (trait.deserialize(traitInfo)) break;
            }
            trait.associatePlayer(player);
        }
        return true;
    }
}
