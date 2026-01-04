package com.metype.mmocraft.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;

public class MainConfig extends ConfigFile<MainConfig> {
    @Override
    protected String getPath() {
        return "config.json";
    }

    @Override
    protected MainConfig defaultConfig() {
        return new MainConfig();
    }

    @Override
    protected Gson getGsonInstance() {
        return new GsonBuilder().setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    public static MainConfig load() {
        return (new MainConfig()).createAndLoad();
    }

    public Map<Integer, List<String>> levelUpCommands = Map.of();
}
