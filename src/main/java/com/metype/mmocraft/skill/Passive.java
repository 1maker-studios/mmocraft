package com.metype.mmocraft.skill;

import com.metype.mmocraft.util.CommandUtils;

import java.util.Map;

public class Passive {
    public String name;
    public String description;
    public String display;
    public float value;

    public Passive(String name, String description, String display, float value) {
        this.name = name;
        this.description = description;
        this.display = display;
        this.value = value;
    }

    public String display() {
        return CommandUtils.format(
                display,
                Map.of(
                        "name", name,
                        "description", description,
                        "value", value
                )
        );
    }
}
