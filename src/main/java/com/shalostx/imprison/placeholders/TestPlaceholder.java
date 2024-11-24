package com.shalostx.imprison.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestPlaceholder extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "test";
    }

    @Override
    public @NotNull String getAuthor() {
        return "TestAuthor";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params.equals("hello")) {
            return "Hello, " + player.getName() + "!";
        }
        return null;
    }
}
