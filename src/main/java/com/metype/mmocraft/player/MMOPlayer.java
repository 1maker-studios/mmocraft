package com.metype.mmocraft.player;

import com.metype.mmocraft.MMOCraft;
import com.metype.mmocraft.interfaces.ISerializable;
import com.metype.mmocraft.skill.ISkill;
import com.metype.mmocraft.skill.SkillManager;
import com.metype.mmocraft.trait.ITrait;
import com.metype.mmocraft.util.DBUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.sql.SQLException;
import java.util.*;

public class MMOPlayer implements ISerializable {
    public UUID playerID;
    private final Map<Identifier, ISkill> skills = new HashMap<>();

    public MMOPlayer(ServerPlayerEntity player) {
        this.playerID = player.getUuid();
        for(ISkill skill : SkillManager.getSkills()) {
            skill.associatePlayer(this);
            skills.put(skill.getID(), skill);
        }
        try {
            DBUtils.getInstance().getPlayer(this);
        } catch (SQLException e) {
            MMOCraft.LOGGER.error("Failed to load playerdata for UUID {}. {}", playerID, e);
        }
    }

    public void save() {
        try {
            DBUtils.getInstance().updatePlayer(this);
        } catch (SQLException e) {
            MMOCraft.LOGGER.error("Failed to save playerdata for UUID {}. {}", playerID, e);
        }
    }

    public String serialize() {
        StringBuilder data = new StringBuilder();
        for(ISkill skill : skills.values()) {
            data.append(skill.serialize());
            data.append("|");
            for(ITrait trait : skill.getTraits()) {
                data.append(trait.serialize());
                data.append("|");
            }
            data.append(";");
        }
        return data.toString();
    }

    public boolean deserialize(String data) {
        if(data == null) return false;
        String[] skillData = data.split(";");
        for(String str : skillData) {
            boolean success = false;
            for(ISkill skill : skills.values()) {
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

    public ISkill getSkill(Identifier id) {
        return skills.getOrDefault(id, null);
    }

    public Collection<ISkill> getSkills() {
        return skills.values().stream().toList();
    }
}
