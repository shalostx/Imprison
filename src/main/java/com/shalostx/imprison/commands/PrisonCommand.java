package com.shalostx.imprison.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.command.CommandSender;

import java.util.Objects;

@CommandAlias("prison|pr|p")
public class PrisonCommand extends BaseCommand {

    @Default
    @Description("Main command for Prison")
    public void onCommand(CommandSender sender) {
        sender.sendMessage("Welcome to Prison!");
    }

    @Subcommand("add")
    @CommandCompletion("red|yellow day")
    @Syntax("<red/yellow>")
    public void onAdd(CommandSender sender, @Single String color, @Optional @Single String day) {
        if (Objects.equals(color, "red")) {
            sender.sendMessage("You are in red Prison!" + day);
        } else if (Objects.equals(color, "yellow")) {
            sender.sendMessage("You are in yellow Prison!" + day);
        }
    }

}
