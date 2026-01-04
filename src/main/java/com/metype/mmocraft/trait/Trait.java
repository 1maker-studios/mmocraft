package com.metype.mmocraft.trait;

import com.metype.mmocraft.interfaces.*;
import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.skill.Skill;
import com.metype.mmocraft.util.UIUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Trait implements INamed, ISerializable, ICopyable, IIdentifiable, IDescribable {
    public static Identifier ID = Identifier.of("mmocraft:trait");

    protected int level = 0;
    protected MMOPlayer player;
    protected Skill parent;

    protected String name = "Trait";

    public void setParent(Skill skill) {
        this.parent = skill;
    }

    public int getLevel() {
        return level;
    }

    public double modulate(double input, double factor) {
        return input * factor;
    }

    public String getActionMessage() {
        return "Trait did thing";
    }

    public boolean levelUp() {
        if(this.parent.getSkillPointsAvailable() <= 0) return false;
        this.parent.useSkillPoint();
        level++;
        return true;
    }

    public void associatePlayer(MMOPlayer player) {
        this.player = player;
    }

    @Override
    public ICopyable copy() {
        return new Trait();
    }

    @Override
    public Text getDescription() {
        return MutableText.of(PlainTextContent.of("Default trait")).setStyle(Style.EMPTY.withColor(UIUtil.plainMessageColor.value()));
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String serialize() {
        return name + "=" + level;
    }

    @Override
    public boolean deserialize(String data) {
        if(data == null) return false;
        String[] dataSplitOnEQ = data.split("=");
        if(dataSplitOnEQ.length < 2) return false;
        if(!dataSplitOnEQ[0].equalsIgnoreCase(name)) return false;
        String[] params = dataSplitOnEQ[1].split(",");
        if(params.length != 1) return false;
        level = Integer.parseInt(params[0]);
        return true;
    }
}
