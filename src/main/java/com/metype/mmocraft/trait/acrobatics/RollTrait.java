package com.metype.mmocraft.trait.acrobatics;

import com.metype.mmocraft.interfaces.ICopyable;
import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.skill.AcrobaticsSkill;
import com.metype.mmocraft.trait.ITrait;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class RollTrait implements ITrait {
    public static Identifier ID = Identifier.of("mmocraft:roll");

    private int level = 0;
    private MMOPlayer player;

    public RollTrait(boolean init) {

    }

    public RollTrait() {

    }

    public RollTrait(int level) {
        this.level = level;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public double modulate(double input, double factor) {
        // Linearly scale roll chance between 0.0 - 0.5 based on trait level, capping at level 100
        // Incorporate factor here just in case down-the-line something like luck or whatever is factored in
        return (Math.min(level, 100.f) / 200) * factor;
    }

    @Override
    public String getName() {
        return "Roll";
    }

    @Override
    public String serialize() {
        return "roll=" + level;
    }

    @Override
    public boolean deserialize(String data) {
        if(data == null) return false;
        String[] dataSplitOnEQ = data.split("=");
        if(dataSplitOnEQ.length < 2) return false;
        if(!dataSplitOnEQ[0].equalsIgnoreCase("roll")) return false;
        String[] params = dataSplitOnEQ[1].split(",");
        if(params.length != 1) return false;
        level = Integer.parseInt(params[0]);
        return true;
    }

    @Override
    public RollTrait copy() {
        return new RollTrait();
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    public static double getPlayerRollChance(@NotNull MMOPlayer player) {
        RollTrait rollTrait = (RollTrait) player.getSkill(AcrobaticsSkill.ID).getTrait(ID);
        return rollTrait.modulate(0, 0);
    }

    @Override
    public String getActionMessage() {
        return "Rolled";
    }

    @Override
    public String getDescription() {
        return "Gives you a +0.5% chance to roll on landing for every level";
    }

    @Override
    public boolean levelUp() {
        AcrobaticsSkill playerSkill = (AcrobaticsSkill) player.getSkill(AcrobaticsSkill.ID);
        if(playerSkill.getSkillPointsAvailable() <= 0) return false;
        playerSkill.useSkillPoint();
        level++;
        player.save();
        return true;
    }

    @Override
    public void associatePlayer(MMOPlayer player) {
        this.player = player;
    }
}
