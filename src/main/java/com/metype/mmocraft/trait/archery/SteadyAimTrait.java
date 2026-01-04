package com.metype.mmocraft.trait.archery;

import com.metype.mmocraft.trait.Trait;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SteadyAimTrait extends Trait {
    public static Identifier ID = Identifier.of("mmocraft:steady_aim");

    public SteadyAimTrait() {
        name = "Steady Aim";
    }

    @Override
    public double modulate(double input, double factor) {
        return 1 - (Math.min(level, 100.f) / 100);
    }

    @Override
    public SteadyAimTrait copy() {
        return new SteadyAimTrait();
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public String getActionMessage() {
        return "";
    }

    @Override
    public Text getDescription() {
        return Text.of("Increases aiming accuracy while crouched");
    }
}
