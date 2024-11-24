package com.shalostx.imprison.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.shalostx.imprison.database.SQLManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;

@CommandAlias("level|lvl")
public class LevelCommand extends BaseCommand {

    private final SQLManager sqlManager;

    // Конструктор, принимающий SQLManager
    public LevelCommand(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    @Default
    public void onCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
        } else {
            Player player = (Player) sender;
            String playerName = player.getName();

            // Асинхронный доступ к базе данных
            org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(player.getServer().getPluginManager().getPlugin("Imprison"), () -> {
                try (Connection connection = sqlManager.getConnection()) {
                    // Получаем уровень игрока из базы данных
                    int level = sqlManager.getPlayerLevel(connection, playerName);

                    if (level != -1) {
                        player.sendMessage("Your current level is: " + level);
                    } else {
                        player.sendMessage("Your level data could not be found.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    player.sendMessage("An error occurred while retrieving your level.");
                }
            });
        }
    }

    @Subcommand("add")
    @CommandCompletion("@players")
    @Syntax("<player>")
    public void onAdd(CommandSender sender, @Single String playerName) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
        }
        Player player = Bukkit.getPlayer(playerName);
        org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(player.getServer().getPluginManager().getPlugin("Imprison"), () -> {
            try (Connection connection = sqlManager.getConnection()) {
                // Получаем уровень игрока из базы данных
                int level = sqlManager.getPlayerLevel(connection, playerName);

                sqlManager.updatePlayerLevel(sqlManager.getConnection(), playerName, level+1);

            } catch (SQLException e) {
                e.printStackTrace();
                player.sendMessage("An error occurred while retrieving your level.");
            }
        });
    }
    @Subcommand("set")
    @CommandCompletion("@players @range:1-20")
    @Syntax("<player> <level>")
    public void onSet(CommandSender sender, @Single String playerName, @Single int level) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
        }
        Player player = Bukkit.getPlayer(playerName);
        org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(player.getServer().getPluginManager().getPlugin("Imprison"), () -> {
            try (Connection connection = sqlManager.getConnection()) {
                sqlManager.updatePlayerLevel(sqlManager.getConnection(), playerName, level);
            } catch (SQLException e) {
                e.printStackTrace();
                player.sendMessage("An error occurred while retrieving your level.");
            }
        });

    }
}
