package com.github.maxopoly.caveworm.commands;

import com.github.maxopoly.caveworm.Caveworm;
import com.github.maxopoly.caveworm.CavewormAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GenerateCave implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED
                    + "Generating caves inside the console seems like a bad idea, let's not do that");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /cwgen <length>");
            return true;
        }
        int length;
        try {
            length = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + args[0] + " is not a valid integer");
            return true;
        }
        CavewormAPI.spawnCaveAt(((Player) sender).getLocation(), length, Caveworm.getWormConfig());
        sender.sendMessage(ChatColor.GREEN + "Done");
        return true;
    }
}
