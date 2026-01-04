package com.metype.mmocraft.player;

import com.metype.mmocraft.MMOCraft;
import com.metype.mmocraft.interfaces.ISerializable;
import com.metype.mmocraft.skill.Skill;
import com.metype.mmocraft.skill.SkillManager;
import com.metype.mmocraft.trait.Trait;
import com.metype.mmocraft.util.DBUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.sql.SQLException;
import java.util.*;

public class MMOPlayer implements ISerializable {
    public UUID uuid;
    public String name;
    private final Map<Identifier, Skill> skills = new HashMap<>();
    private UserConfig config = UserConfig.defaultConf();

    public MMOPlayer(ServerPlayerEntity player) {
        this.uuid = player.getUuid();
        this.name = player.getStringifiedName();
        for(Skill skill : SkillManager.getSkills()) {
            skill.associatePlayer(this);
            skills.put(skill.getID(), skill);
        }
        try {
            DBUtils.getInstance().getPlayer(this);
        } catch (SQLException e) {
            MMOCraft.LOGGER.error("Failed to load playerdata for UUID {}. {}", uuid, e);
        }
    }

    public void save() {
        try {
            DBUtils.getInstance().updatePlayer(this);
        } catch (SQLException e) {
            MMOCraft.LOGGER.error("Failed to save playerdata for UUID {}. {}", uuid, e);
        }
    }

    public String serialize() {
        StringBuilder data = new StringBuilder();
        for(Skill skill : skills.values()) {
            data.append(skill.serialize());
            data.append("|");
            for(Trait trait : skill.getTraits()) {
                data.append(trait.serialize());
                data.append("|");
            }
            data.append(";");
        }
        data.append(" + ");
        data.append(config.serialize());
        return data.toString();
    }

    public boolean deserialize(String data) {
        if(data == null) return false;
        String[] confData = data.split(" \\+ ");
        if(confData.length > 1) {
            config = UserConfig.deserialize(confData[1]);
        } else {
            config = UserConfig.defaultConf();
        }
        String[] skillData = confData[0].split(";");
        for(String str : skillData) {
            boolean success = false;
            for(Skill skill : skills.values()) {
                if(skill.deserialize(str)) {
                    success = true;
                    break;
                }
            }
            if(!success) {
                MMOCraft.LOGGER.warn("Failed to deserialize \"{}\", some skill data may be lost.", str);
            }
        }
        return true;
    }

    public boolean shouldShowBossBar() {
        return config == null || config.showBossBar;
    }

    public void setShowBossBar(boolean value) {
        config.showBossBar = value;
        save();
    }

    public Skill getSkill(Identifier id) {
        return skills.getOrDefault(id, null);
    }

    public Collection<Skill> getSkills() {
        return skills.values().stream().toList();
    }

    public int powerLevel() {
        return SkillManager.calculatePowerLevel(this);
    }
}
