package com.metype.mmocraft.util;

import com.metype.mmocraft.player.MMOPlayer;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class DBUtils {
    private Connection connection;
    private Statement statement;
    private PreparedStatement updateUserStatement;
    private PreparedStatement getUserStatement;

    private static DBUtils instance;

    public static void init() throws SQLException {
        if (instance == null) {
            instance = new DBUtils();
        }
        instance.connection = DriverManager.getConnection("jdbc:sqlite:config/mmocraft.db");
        instance.statement = instance.connection.createStatement();
        instance.statement.setQueryTimeout(10);
        instance.statement.execute("create table if not exists players (key string PRIMARY KEY, skills string)");
        instance.updateUserStatement = instance.connection.prepareStatement("insert into players values(?, ?) on conflict(key) do update SET skills=excluded.skills");
        instance.getUserStatement = instance.connection.prepareStatement("select * from players where key = ?");
    }

    public static DBUtils getInstance() {
        return instance;
    }

    public void updatePlayer(@NotNull MMOPlayer player) throws SQLException {
        updateUserStatement.setString(1, player.uuid.toString());
        updateUserStatement.setString(2, player.serialize());
        updateUserStatement.executeUpdate();
    }

    public void getPlayer(@NotNull MMOPlayer player) throws SQLException {
        getUserStatement.setString(1, player.uuid.toString());
        ResultSet results = getUserStatement.executeQuery();
        player.deserialize(results.getString("skills"));
    }
}
