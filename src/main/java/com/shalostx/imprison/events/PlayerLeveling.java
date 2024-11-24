package com.shalostx.imprison.events;

import com.shalostx.imprison.database.SQLManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.SQLException;

public class PlayerLeveling implements Listener {

    private final SQLManager sqlManager;

    // Конструктор для получения SQLManager
    public PlayerLeveling(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName(); // Получаем ник игрока

        // Выполняем асинхронную задачу для работы с базой данных
        org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(event.getPlayer().getServer().getPluginManager().getPlugin("Imprison"), () -> {
            try (Connection connection = sqlManager.getConnection()) {
                // Проверяем, есть ли игрок с таким ником в базе данных
                if (!sqlManager.playerExists(connection, playerName)) {
                    // Если игрока нет, добавляем его в базу данных с уровнем 1
                    sqlManager.addNewPlayer(connection, playerName);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                org.bukkit.Bukkit.getScheduler().runTask(event.getPlayer().getServer().getPluginManager().getPlugin("Imprison"), () ->
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>[Ошибка]<r> Возникла проблема во время поиска ваших данных, обратитесь к администратору."))
                );
            }
        });
    }
}
