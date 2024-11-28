package com.shalostx.imprison.placeholders;

import com.shalostx.imprison.Imprison;
import com.shalostx.imprison.database.SQLManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class PlayerLevelPlaceholder extends PlaceholderExpansion {

    private final SQLManager sqlmanager;
    private final Imprison plugin;

    public PlayerLevelPlaceholder(SQLManager sqlManager, Imprison plugin) {
        this.plugin = plugin;
        this.sqlmanager = sqlManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "prison";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Shalostx";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    public String onPlaceholderRequest(Player player, String params) {
        if (player == null || params == null) return "";
        if (params.contains("level")) {
            String level;
            try {
                 level = String.valueOf(sqlmanager.getPlayerLevel(sqlmanager.getConnection(), player.getName()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return level;
        }
        return "";

    }
}
