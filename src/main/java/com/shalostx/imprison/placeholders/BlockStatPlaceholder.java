package com.shalostx.imprison.placeholders;

import com.shalostx.imprison.Imprison;
import com.shalostx.imprison.database.SQLManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class BlockStatPlaceholder extends PlaceholderExpansion {

    private final SQLManager sqlmanager;
    private final Imprison plugin;

    public BlockStatPlaceholder(SQLManager sqlManager, Imprison plugin) {
        this.plugin = plugin;
        this.sqlmanager = sqlManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "blockstat";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Shalostx";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) {
            return null;
        }

        String blockType = params.toUpperCase();
        Material material = Material.getMaterial(blockType);

        if (material == null) {
            plugin.getLogger().warning("Указан некорректный тип блока: " + blockType);
            return "0";
        }

        if (!material.isBlock()) {
            plugin.getLogger().warning("Указанный материал не является блоком: " + blockType);
            return "0";
        }

        int blockCount;
        try {
            blockCount = sqlmanager.getBlockCount(sqlmanager.getConnection(), player.getName(), blockType);
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка при запросе данных из базы: " + e.getMessage());
            return "0";
        }

        return String.valueOf(blockCount);
    }
}
