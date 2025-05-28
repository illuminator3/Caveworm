package com.github.maxopoly.caveworm.commands;

import com.github.maxopoly.caveworm.Caveworm;
import com.github.maxopoly.caveworm.CavewormAPI;
import com.github.maxopoly.caveworm.distribution.GlobalDistributor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SeedArea implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        GlobalDistributor dist = CavewormAPI.getDistributer(Caveworm.getWormConfig());
        if (dist == null) {
            sender.sendMessage(ChatColor.RED + "Could not initiate seeding, check console log for more information");
            return true;
        }
        sender.sendMessage(ChatColor.GOLD + "Beginning to seed. This may take a while");
        dist.distribute();
        return true;
    }
}
