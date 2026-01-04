package com.metype.mmocraft;

import com.metype.mmocraft.command.ICommand;
import com.metype.mmocraft.command.skill.SkillCommand;
import com.metype.mmocraft.command.config.ConfigCommand;
import com.metype.mmocraft.command.trait.LumberjackCommand;
import com.metype.mmocraft.command.trait.TraitCommand;
import com.metype.mmocraft.config.MainConfig;
import com.metype.mmocraft.player.PlayerAdapter;
import com.metype.mmocraft.skill.*;
import com.metype.mmocraft.trait.acrobatics.RollTrait;
import com.metype.mmocraft.trait.archery.SteadyAimTrait;
import com.metype.mmocraft.trait.woodcutting.LumberjackTrait;
import com.metype.mmocraft.util.ChunkManager;
import com.metype.mmocraft.util.DBUtils;
import com.metype.mmocraft.util.UIUtil;
import com.metype.mmocraft.util.Utils;
import com.metype.mmocraft.player.UserConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.WorldEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MMOCraft implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(MMOCraft.class);
    public static MinecraftServer SERVER;
    public static final String MOD_ID = "MMOCraft";
    public static MainConfig MAIN_CONFIG;
    public static UserConfig DEFAULT_USER_CONFIG;
    public static ChunkManager CHUNK_MANAGER;

    private static final AtomicInteger nextSave = new AtomicInteger(0);

    private static final List<ICommand> COMMANDS = List.of(
            new ConfigCommand(),
            new SkillCommand(),
            new TraitCommand(),
            new LumberjackCommand()
    );

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            SERVER = minecraftServer;
        });

        ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> {
            UIUtil.tickUiElements();
            Utils.tick(minecraftServer);
            if(minecraftServer.getTicks() > nextSave.get()) {
                PlayerAdapter.saveAllPlayers();
                nextSave.set(minecraftServer.getTicks() + (20 * 60 * 5)); // 5 minutes
            }
        });

        CHUNK_MANAGER = new ChunkManager();

        ServerChunkEvents.CHUNK_LOAD.register(CHUNK_MANAGER::chunkLoad);
        ServerChunkEvents.CHUNK_UNLOAD.register(CHUNK_MANAGER::chunkUnload);

        try {
            DBUtils.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        registerSkills();
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
        MAIN_CONFIG = MainConfig.load();
        DEFAULT_USER_CONFIG = UserConfig.load();
    }

    private void registerSkills() {
        SkillManager.registerSkill(AcrobaticsSkill.defaultSkill());
        SkillManager.registerTrait(AcrobaticsSkill.ID, new RollTrait());

        SkillManager.registerSkill(MiningSkill.defaultSkill());

        SkillManager.registerSkill(UnarmedSkill.defaultSkill());

        SkillManager.registerSkill(SwordsSkill.defaultSkill());

        SkillManager.registerSkill(AxesSkill.defaultSkill());

        SkillManager.registerSkill(ArcherySkill.defaultSkill());
        SkillManager.registerTrait(ArcherySkill.ID, new SteadyAimTrait());

        SkillManager.registerSkill(WoodcuttingSkill.defaultSkill());
        SkillManager.registerTrait(WoodcuttingSkill.ID, new LumberjackTrait());
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        for(ICommand command : COMMANDS) {
            dispatcher.register(command.build());
        }
    }
}
