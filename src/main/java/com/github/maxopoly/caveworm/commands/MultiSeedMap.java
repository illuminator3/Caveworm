package com.github.maxopoly.caveworm.commands;

import com.github.maxopoly.caveworm.Caveworm;
import com.github.maxopoly.caveworm.CavewormAPI;
import com.github.maxopoly.caveworm.WormConfig;
import com.github.maxopoly.caveworm.distribution.GlobalDistributor;
import com.github.maxopoly.caveworm.distribution.JobQueue;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Set;

public class MultiSeedMap implements CommandExecutor {
    private Set<GlobalDistributor> distributorsWaiting;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Caveworm plugin = Caveworm.getInstance();
        File folder = plugin.getDataFolder();
        if (!folder.isDirectory()) {
            sender.sendMessage(ChatColor.RED
                    + "The plugin directory for this plugin doesn't exist");
            return true;
        }

        for (File possibleConfig : folder.listFiles()) {
            sender.sendMessage("Attempting to parse " + possibleConfig.getName());
            if (possibleConfig.isDirectory()) {
                sender.sendMessage(ChatColor.RED + "Found directory "
                        + possibleConfig.getName()
                        + " in plugin folder, ignoring it");
                continue;
            }
            if (!possibleConfig.getName().endsWith(".yml")) {
                sender.sendMessage(ChatColor.RED + "Found non yaml file "
                        + possibleConfig.getName()
                        + " in plugin folder, ignoring it");
                continue;
            }
            YamlConfiguration yamlConfig = YamlConfiguration
                    .loadConfiguration(possibleConfig);
            if (yamlConfig == null) {
                sender.sendMessage(ChatColor.RED + "Failed to load file "
                        + possibleConfig.getName()
                        + " in plugin folder, ignoring it");
                continue;
            }
            WormConfig config = new WormConfig();
            config.parse(plugin, yamlConfig);

            GlobalDistributor dist = CavewormAPI.getDistributer(config);
            if (dist == null) {
                sender.sendMessage(ChatColor.RED
                        + "Could not initiate seeding, check console log for more information");
                continue;
            }
            JobQueue jobs = JobQueue.getInstance();
            plugin.info(ChatColor.GOLD + "Adding job based on config " + possibleConfig.getName() + " to queue ");
            jobs.addJob(dist);
        }
        return true;
    }
}