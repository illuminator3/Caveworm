package com.github.maxopoly.caveworm;

import com.github.maxopoly.caveworm.caveformers.CaveFormer;
import com.github.maxopoly.caveworm.caveformers.SimplexSphereFormer;
import com.github.maxopoly.caveworm.distribution.GlobalDistributor;
import com.github.maxopoly.caveworm.events.CaveWormGenerationCompletionEvent;
import com.github.maxopoly.caveworm.worms.SimplexNoiseWorm;
import com.github.maxopoly.caveworm.worms.Worm;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class CavewormAPI {

    public static Worm getWorm(Location loc, int length, WormConfig config) {
        if (config.getWormType().equals("Simplexworm")) {
            return new SimplexNoiseWorm(loc, config.getXMovementOctaves(),
                    config.getYMovementOctaves(), config.getZMovementOctaves(),
                    config.getXSpreadFrequency(), config.getYSpreadFrequency(),
                    config.getZSpreadFrequency(),
                    config.getXSpreadThreshHold(),
                    config.getYSpreadThreshHold(),
                    config.getZSpreadThreshHold(),
                    config.getXSpreadAmplitude(), config.getYSpreadAmplitude(),
                    config.getYSpreadAmplitude(), length,
                    config.getXWormMovementSeed(),
                    config.getYWormMovementSeed(),
                    config.getZWormMovementSeed());
        }
        return null;
    }

    public static SimplexSphereFormer getCaveFormer(WormConfig config, int id) {
        if (config.getFormingType().equals("SimplexSphere")) {
            return new SimplexSphereFormer(id, config.getFormingFillMaterial(),
                    config.getXFormingOctaveCount(),
                    config.getYFormingOctaveCount(),
                    config.getZFormingOctaveCount(),
                    config.getXFormingFrequency(),
                    config.getYFormingFrequency(),
                    config.getZFormingFrequency(),
                    config.getXUpperFormingRadiusBound(),
                    config.getYUpperFormingRadiusBound(),
                    config.getZUpperFormingRadiusBound(),
                    config.getXLowerFormingRadiusBound(),
                    config.getYLowerFormingRadiusBound(),
                    config.getZLowerFormingRadiusBound(), config.getXZSlices(),
                    config.getXYSlices(), config.getYZSlices(),
                    config.getIgnoreMaterials(),
                    config.getFallingBlockBehavior(),
                    config.getFallingBlockReplacement(),
                    config.getXFillingSeed(), config.getYFillingSeed(),
                    config.getZFillingSeed());
        }
        return null;
    }

    public static void spawnCaveAt(Location loc, int length, WormConfig config) {
        Worm w = getWorm(loc, length, config);
        CaveFormer former = getCaveFormer(config, 0);

        w.forEachRemaining(former::extendLocation);

        System.out.println("Firing event"); // TODO remove this

        Bukkit.getPluginManager().callEvent(new CaveWormGenerationCompletionEvent(config));
    }

    public static GlobalDistributor getDistributer(WormConfig config) {
        if (config.getDistributionArea() == null) {
            Caveworm.getInstance().warning(
                    "No area loaded, can't get distributor");
            return null;
        }

        return new GlobalDistributor(config,
                config.getDistributionSeed(), 39);
    }
}
