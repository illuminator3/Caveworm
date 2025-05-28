package com.github.maxopoly.caveworm.commands;

import com.github.maxopoly.caveworm.Caveworm;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadConfig implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Caveworm.getInstance().refreshConfig();
        sender.sendMessage(ChatColor.GREEN + "Reloaded config");
        return true;
    }
}
