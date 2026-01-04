package com.metype.mmocraft.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.metype.mmocraft.MMOCraft;
import com.metype.mmocraft.config.ConfigFile;

public class UserConfig extends ConfigFile<UserConfig> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private UserConfig() {}

    @Override
    protected String getPath() {
        return "default_userconfig.json";
    }

    @Override
    protected UserConfig defaultConfig() {
        return defaultConf();
    }

    @Override
    protected Gson getGsonInstance() {
        return GSON;
    }

    public static UserConfig defaultConf() {
        return MMOCraft.DEFAULT_USER_CONFIG == null ? new UserConfig() : MMOCraft.DEFAULT_USER_CONFIG;
    }

    public static UserConfig load() {
        return (new UserConfig()).createAndLoad();
    }

    public String serialize() {
        return GSON.toJson(this, UserConfig.class);
    }

    public static UserConfig deserialize(String data) {
        return GSON.fromJson(data, UserConfig.class);
    }

    public boolean showBossBar = true;
}
