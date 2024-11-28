package com.shalostx.imprison.database;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SQLManager {

    private final String databasePath;
    private final JavaPlugin plugin;


    public SQLManager(String pluginFolderPath, JavaPlugin plugin) {
        this.plugin = plugin;
        // Создаем папку плагина, если она не существует
        File pluginFolder = new File(pluginFolderPath);
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        // Путь к базе данных
        File dbFile = new File(pluginFolder, "imprison.db");
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.databasePath = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        initializeDatabase();
    }

    // Метод для получения соединения с базой данных
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databasePath);
    }

    // Инициализация базы данных и создание таблицы
    private void initializeDatabase() {
        try (Connection connection = getConnection()) {
            // Создание таблицы players
            String createPlayersTableSQL = "CREATE TABLE IF NOT EXISTS players (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT NOT NULL UNIQUE, " + "level INTEGER NOT NULL" + ");";
            try (PreparedStatement statement = connection.prepareStatement(createPlayersTableSQL)) {
                statement.executeUpdate();
            }

            // Создание таблицы player_blocks
            String createPlayerBlocksTableSQL =
                    "CREATE TABLE IF NOT EXISTS player_blocks (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "player_name TEXT NOT NULL, " +
                            "block_name TEXT NOT NULL, " +
                            "block_count INTEGER NOT NULL DEFAULT 0, " +
                            "UNIQUE (player_name, block_name), " + // Composite primary key
                            "FOREIGN KEY (player_name) REFERENCES players(name)" +
                            ");";
            try (PreparedStatement statement = connection.prepareStatement(createPlayerBlocksTableSQL)) {
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для проверки, существует ли игрок с данным ником
    public boolean playerExists(Connection connection, String name) throws SQLException {
        String query = "SELECT 1 FROM players WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    // Метод для добавления или обновления количества блоков игрока
    public void incrementBlockCount(Connection connection, String playerName, String blockName, int value) throws SQLException {

        // Проверяем, существует ли запись для блока
        String selectBlockQuery = "SELECT block_count FROM player_blocks WHERE player_name = ? AND block_name = ?";
        boolean blockExists = false;



        try (PreparedStatement selectBlockStmt = connection.prepareStatement(selectBlockQuery)) {
            selectBlockStmt.setString(1, playerName);
            selectBlockStmt.setString(2, blockName);
            try (var resultSet = selectBlockStmt.executeQuery()) {
                if (resultSet.next()) {
                    blockExists = true;
                }
            }
        }

        // Обновляем или добавляем данные
        if (blockExists) {
            String updateBlockQuery = "UPDATE player_blocks SET block_count = block_count + ? WHERE player_name = ? AND block_name = ?";
            try (PreparedStatement updateBlockStmt = connection.prepareStatement(updateBlockQuery)) {
                updateBlockStmt.setInt(1, value);
                updateBlockStmt.setString(2, playerName);
                updateBlockStmt.setString(3, blockName);
                updateBlockStmt.executeUpdate();
            }
        } else {
            String insertBlockQuery = "INSERT INTO player_blocks (player_name, block_name, block_count) VALUES (?, ?, ?)";
            try (PreparedStatement insertBlockStmt = connection.prepareStatement(insertBlockQuery)) {
                insertBlockStmt.setString(1, playerName);
                insertBlockStmt.setString(2, blockName);
                insertBlockStmt.setInt(3, value);
                insertBlockStmt.executeUpdate();
            }
        }
    }

    // Метод для получения количеста блоков у игрока
    public int getBlockCount(Connection connection, String playerName, String blockName) throws SQLException {
        String query = "SELECT block_count FROM player_blocks WHERE player_name = ? AND block_name = ?";
        try (PreparedStatement selectBlockStmt = connection.prepareStatement(query)) {
            selectBlockStmt.setString(1, playerName);
            selectBlockStmt.setString(2, blockName);
            try (var resultSet = selectBlockStmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("block_count");
                }
                return 0;
            }
        }
    }

    // Метод для добавления нового игрока
    public void addNewPlayer(Connection connection, String name) throws SQLException {
        String insert = "INSERT INTO players (name, level) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insert)) {
            statement.setString(1, name);
            statement.setInt(2, 1); // Начальный уровень
            statement.executeUpdate();
        }
    }

    // Метод для получения уровня игрока
    public int getPlayerLevel(Connection connection, String name) throws SQLException {
        String query = "SELECT level FROM players WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("level");
                }
                return -1; // Если игрок не найден
            }
        }
    }

    // Метод для обновления уровня игрока
    public void updatePlayerLevel(Connection connection, String name, int level) throws SQLException {
        String update = "UPDATE players SET level = ? WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(update)) {
            statement.setInt(1, level);
            statement.setString(2, name);
            statement.executeUpdate();
        }
    }

    final Map<String, Map<String, Integer>> blockCache = new ConcurrentHashMap<>();

    public void updateCache(String playerName, String blockName, int blockCount) {
        blockCache
                .computeIfAbsent(playerName, k -> new ConcurrentHashMap<>())
                .merge(blockName, blockCount, Integer::sum);
    }

    public void startDatabaseUpdateTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try (Connection connection = getConnection()) {
                if (blockCache.isEmpty()) {
                    return;
                }
                for (Map.Entry<String, Map<String, Integer>> entry : blockCache.entrySet()) {
                    String playerName = entry.getKey();
                    for (Map.Entry<String, Integer> blockEntry : entry.getValue().entrySet()) {
                        String blockName = blockEntry.getKey();
                        int blockCount = blockEntry.getValue();

                        incrementBlockCount(connection, playerName, blockName, blockCount);
                    }
                }
                // Убираем мусор нахой
                blockCache.clear();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, 0L, 200L); // 10 seconds
    }


}
