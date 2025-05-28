package com.github.maxopoly.caveworm;

import com.github.maxopoly.caveworm.commands.CavewormCommandHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Caveworm extends JavaPlugin {
    private static WormConfig config;
    private static Caveworm instance;

    public void onEnable() {
        instance = this;

        new CavewormCommandHandler(this);

        config = new WormConfig();
        refreshConfig();
    }

    /**
     * Simple WARNING level logging.
     */
    public void warning(String message) {
        getLogger().log(Level.WARNING, message);
    }

    /**
     * Simple INFO level logging
     */
    public void info(String message) {
        getLogger().log(Level.INFO, message);
    }

    /**
     * Live activatable debug message (using plugin's config.yml top level debug tag to decide) at
     * INFO level.
     * <p>
     * Skipped if DebugLog is false.
     */
    public void debug(String message) {
//        if (isDebugEnabled()) { TODO
//            getLogger().log(Level.INFO, message);
//        }
    }

    /**
     * Simple SEVERE level logging.
     */
    public void severe(String message) {
        getLogger().log(Level.SEVERE, message);
    }

    public static WormConfig getWormConfig() {
        return config;
    }

    public static Caveworm getInstance() {
        return instance;
    }

    public void refreshConfig() {
        saveDefaultConfig();
        reloadConfig();
        config.parse(this, getConfig());
    }
}