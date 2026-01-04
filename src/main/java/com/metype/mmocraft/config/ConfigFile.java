package com.metype.mmocraft.config;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class ConfigFile<T extends ConfigFile<T>> {
    protected String getPath() {
        throw new RuntimeException("Must override ConfigFile.getPath()");
    }

    protected T defaultConfig() {
        throw new RuntimeException("Must override ConfigFile.defaultConfig()");
    }

    protected Class<T> getConfigClass() {
        return (Class<T>) getClass();
    }

    protected Gson getGsonInstance() {
        throw new RuntimeException("Must override ConfigFile.getGsonInstance()");
    }

    public T createAndLoad() {
        try {
            Reader reader = ConfigUtils.getFileReaderForConfigFile(getPath());
            Gson gson = getGsonInstance();
            T config = gson.fromJson(reader, getConfigClass());
            if(config == null) {
                config = defaultConfig();
            }
            config.save();
            return config;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JsonSyntaxException e) {
            return defaultConfig();
        }
    }

    public void save() {
        try {
            Writer writer = ConfigUtils.getFileWriterForConfigFile(getPath());
            Gson gson = getGsonInstance();
            writer.write(gson.toJson(this));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
