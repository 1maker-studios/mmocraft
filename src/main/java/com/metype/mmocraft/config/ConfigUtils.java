package com.metype.mmocraft.config;

import com.metype.mmocraft.MMOCraft;
import com.metype.mmocraft.player.MMOPlayer;
import com.metype.mmocraft.util.CommandUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.Map;

public class ConfigUtils {

    private static String ensureConfigExists(String configFileName) throws IOException {
        FabricLoader loader = FabricLoader.getInstance();
        String configFilePath = loader.getConfigDir() + "/" + MMOCraft.MOD_ID + "/" + configFileName;
        if (!new File(configFilePath).exists()) {
            File configFile = new File(configFilePath);
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
        }
        return configFilePath;
    }

    public static Reader getFileReaderForConfigFile(String fileName) throws IOException {
        return new InputStreamReader(new FileInputStream(ensureConfigExists(fileName)));
    }

    public static Writer getFileWriterForConfigFile(String fileName) throws IOException {
        return new OutputStreamWriter(new FileOutputStream(ensureConfigExists(fileName)));
    }

    public static void checkRunLevelUpCommand(MMOPlayer player, int level) {
        if(MMOCraft.MAIN_CONFIG.levelUpCommands.containsKey(level)) {
            MMOCraft.MAIN_CONFIG.levelUpCommands.get(level).forEach(cmd ->
                MMOCraft.SERVER.getCommandManager().parseAndExecute(
                    MMOCraft.SERVER.getCommandSource(),
                    CommandUtils.format(
                            cmd,
                            Map.of(
                                    "player", player.name,
                                    "level", level
                            )
                    )
                )
            );
        }
    }
}

