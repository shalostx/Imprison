package com.shalostx.imprison;

import co.aikar.commands.PaperCommandManager;
import com.shalostx.imprison.commands.LevelCommand;
import com.shalostx.imprison.commands.PrisonCommand;
import com.shalostx.imprison.database.SQLManager;
import com.shalostx.imprison.events.BlockBreaking;
import com.shalostx.imprison.events.PlayerLeveling;
import com.shalostx.imprison.placeholders.BlockStatPlaceholder;
import com.shalostx.imprison.placeholders.PlayerLevelPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Imprison extends JavaPlugin {

    private SQLManager sqlManager;

    @Override
    public void onEnable() {
        // Путь к папке данных плагина
        String dataFolderPath = getDataFolder().getAbsolutePath();

        // Создание SQLManager
        sqlManager = new SQLManager(dataFolderPath, this);

        // Регистрация UpdateTask
        sqlManager.startDatabaseUpdateTask();

        // Регистрация PlaceholderAPI
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new BlockStatPlaceholder(sqlManager, this).register();
            new PlayerLevelPlaceholder(sqlManager, this).register();
        }

        // ACF Init
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new PrisonCommand());
        manager.registerCommand(new LevelCommand(sqlManager));

        // Регистрация обработчика событий
        getServer().getPluginManager().registerEvents(new PlayerLeveling(sqlManager), this);
        getServer().getPluginManager().registerEvents(new BlockBreaking(sqlManager), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
