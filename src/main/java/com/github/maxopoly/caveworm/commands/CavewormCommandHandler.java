package com.github.maxopoly.caveworm.commands;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class CavewormCommandHandler {
    public CavewormCommandHandler(JavaPlugin plugin) {
        registerCommands(plugin);
    }

    public void registerCommands(JavaPlugin plugin) {
        PluginCommand cwgen = plugin.getCommand("cwgen");
        cwgen.setExecutor(new GenerateCave());
        cwgen.setDescription("Spawns a cave at your current location");
        cwgen.setUsage("/cwgen <length>");

        PluginCommand cwreload = plugin.getCommand("cwreload");
        cwreload.setExecutor(new GenerateCave());
        cwreload.setDescription("Reloads the config");
        cwreload.setUsage("/cwreload");

        PluginCommand cwseed = plugin.getCommand("cwseed");
        cwseed.setExecutor(new SeedArea());
        cwseed.setDescription("Begins distributing caves around the map as specified in the config");
        cwseed.setUsage("/cwseed");

        PluginCommand cwmultiseed = plugin.getCommand("cwmultiseed");
        cwmultiseed.setExecutor(new GenerateCave());
        cwmultiseed.setDescription("Begins distributing caves around the map as specified in any yml files in the config folder");
        cwmultiseed.setUsage("/cwmultiseed");
    }
}
