package com.metype.mmocraft.trait.acrobatics;

import com.metype.mmocraft.trait.Trait;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RollTrait extends Trait {
    public static Identifier ID = Identifier.of("mmocraft:roll");

    public RollTrait() {
        name = "Roll";
    }

    @Override
    public double modulate(double input, double factor) {
        // Linearly scale roll chance between 0.0 - 0.5 based on trait level, capping at level 100
        // Incorporate factor here just in case down-the-line something like luck or whatever is factored in
        return (Math.min(level, 100.f) / 200) * factor;
    }

    @Override
    public RollTrait copy() {
        return new RollTrait();
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public String getActionMessage() {
        return "Rolled";
    }

    @Override
    public Text getDescription() {
        return Text.of("Gives you a +0.5% chance to roll on landing for every level");
    }
}
