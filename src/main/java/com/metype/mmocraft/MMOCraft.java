package com.metype.mmocraft;

import com.metype.mmocraft.command.SkillCommand;
import com.metype.mmocraft.command.TraitCommand;
import com.metype.mmocraft.skill.AcrobaticsSkill;
import com.metype.mmocraft.skill.MiningSkill;
import com.metype.mmocraft.skill.SkillManager;
import com.metype.mmocraft.trait.acrobatics.RollTrait;
import com.metype.mmocraft.util.DBUtils;
import com.metype.mmocraft.util.UIUtil;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class MMOCraft implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(MMOCraft.class);
    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            SERVER = minecraftServer;
        });

        ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> {
            UIUtil.tickUiElements();
        });

        try {
            DBUtils.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        registerSkills();
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
    }

    private void registerSkills() {
        SkillManager.registerSkill(new AcrobaticsSkill(true));
        SkillManager.registerTrait(AcrobaticsSkill.ID, new RollTrait(true));

        SkillManager.registerSkill(new MiningSkill(true));
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        new SkillCommand().register(dispatcher);
        new TraitCommand().register(dispatcher);
    }
}
