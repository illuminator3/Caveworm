package com.github.maxopoly.caveworm.util;

import com.github.maxopoly.caveworm.areas.EllipseArea;
import com.github.maxopoly.caveworm.areas.GlobalYLimitedArea;
import com.github.maxopoly.caveworm.areas.IArea;
import com.github.maxopoly.caveworm.areas.RectangleArea;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.logging.Logger;

public class ConfigParsing {
    private static final Logger log = Bukkit.getLogger();

    public static IArea parseArea(ConfigurationSection config) {
        if (config == null) {
            log.warning("Tried to parse area on null section");
            return null;
        }
        String type = config.getString("type");
        if (type == null) {
            log.warning("Found no area type at " + config.getCurrentPath());
            return null;
        }
        int lowerYBound = config.getInt("lowerYBound", 0);
        int upperYBound = config.getInt("upperYBound", 255);
        String worldName = config.getString("world");
        if (worldName == null) {
            log.warning("Found no world specified for area at " + config.getCurrentPath());
            return null;
        }
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            log.warning("Found no world with name " + worldName + " as specified at " + config.getCurrentPath());
            return null;
        }
        Location center = null;
        if (config.isConfigurationSection("center")) {
            ConfigurationSection centerSection = config.getConfigurationSection("center");
            int x = centerSection.getInt("x", 0);
            int y = centerSection.getInt("y", 0);
            int z = centerSection.getInt("z", 0);
            if (world != null) {
                center = new Location(world, x, y, z);
            }
        }
        int xSize = config.getInt("xSize", -1);
        int zSize = config.getInt("zSize", -1);
        IArea area = null;
        switch (type) {
            case "GLOBAL":
                area = new GlobalYLimitedArea(lowerYBound, upperYBound, world);
                break;
            case "ELLIPSE":
                if (center == null) {
                    log.warning("Found no center for area at " + config.getCurrentPath());
                    return null;
                }
                if (xSize == -1) {
                    log.warning("Found no xSize for area at " + config.getCurrentPath());
                    return null;
                }
                if (zSize == -1) {
                    log.warning("Found no zSize for area at " + config.getCurrentPath());
                    return null;
                }
                area = new EllipseArea(lowerYBound, upperYBound, center, xSize, zSize);
                break;
            case "RECTANGLE":
                if (center == null) {
                    log.warning("Found no center for area at " + config.getCurrentPath());
                    return null;
                }
                if (xSize == -1) {
                    log.warning("Found no xSize for area at " + config.getCurrentPath());
                    return null;
                }
                if (zSize == -1) {
                    log.warning("Found no zSize for area at " + config.getCurrentPath());
                    return null;
                }
                area = new RectangleArea(lowerYBound, upperYBound, center, xSize, zSize);
                break;
            default:
                log.warning("Invalid area type " + type + " at " + config.getCurrentPath());
        }
        return area;
    }
}