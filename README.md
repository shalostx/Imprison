# Imprison

## На русском

**Imprison** — это мощный плагин Minecraft для создания уникального **Prison-сервера**. Плагин предоставляет функционал для уровней игроков, инструментов, брони, а также управления шахтами с доступом на основе уровня.

### Основные функции:
- **Уровни игроков**: Продвигайтесь по уровням, выполняя задачи, такие как добыча блоков.
- **Уровни инструментов и брони**: Улучшайте ваше оборудование с ростом вашего прогресса.
- **Шахты**: Доступ к различным шахтам определяется уровнем игрока.
- **Учет добычи**: Отслеживание добытых блоков для каждого игрока.
- **База данных**: Использование SQLite для оффлайн-режима и MariaDB для онлайн-хранения данных.

### Возможности:
- Поддержка динамического добавления добываемых блоков.
- Интеграция с Aikar’s Command Framework (ACF) для удобной работы с командами.
- Легкая настройка и поддержка.

### Установка:
1. Скачайте плагин из [релизов](#) и поместите его в папку `plugins`.
2. Запустите сервер, чтобы плагин автоматически создал базу данных и начальные файлы конфигурации.
3. Настройте параметры базы данных в файле `config.yml`, если требуется.
4. Перезапустите сервер.

### Использование:
- Список доступных команд можно найти с помощью `/imprison help`.
- Система уровней и шахт настроена для масштабируемости. Вы можете легко добавлять новые уровни и шахты через базу данных.

### Требования:
- Minecraft версия **1.21.1**.
- Сервер Spigot/Paper.
- **MariaDB** или SQLite для хранения данных.

---

## In English

**Imprison** is a powerful Minecraft plugin designed to create a unique **Prison server** experience. It offers functionality for player levels, tool and armor progression, and mine access based on level.

### Core Features:
- **Player Levels**: Level up by completing tasks like mining blocks.
- **Tool and Armor Levels**: Upgrade your equipment as you progress.
- **Mines**: Access various mines based on your player level.
- **Block Tracking**: Track the blocks mined by each player.
- **Database Integration**: Utilizes SQLite for offline mode and MariaDB for online data storage.

### Capabilities:
- Dynamic addition of new mineable blocks.
- Integration with Aikar’s Command Framework (ACF) for streamlined command handling.
- Easy setup and customization.

### Installation:
1. Download the plugin from the [Releases](#) section and place it in the `plugins` folder.
2. Start the server to let the plugin generate the database and initial configuration files.
3. Configure the database parameters in `config.yml` if needed.
4. Restart the server.

### Usage:
- View available commands with `/imprison help`.
- The leveling and mining system is designed for scalability, making it easy to add new levels and mines through the database.

### Requirements:
- Minecraft version **1.21.1**.
- Spigot/Paper server.
- **MariaDB** or SQLite for data storage.

---

Feel free to contribute to the project or report issues on the [GitHub Issues](#) page!
