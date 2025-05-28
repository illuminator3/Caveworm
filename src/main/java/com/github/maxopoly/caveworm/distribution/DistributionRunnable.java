package com.github.maxopoly.caveworm.distribution;

import com.github.maxopoly.caveworm.Caveworm;
import com.github.maxopoly.caveworm.CavewormAPI;
import com.github.maxopoly.caveworm.WormConfig;
import com.github.maxopoly.caveworm.areas.IArea;
import com.github.maxopoly.caveworm.areas.PseudoChunk;
import com.github.maxopoly.caveworm.caveformers.SimplexSphereFormer;
import com.github.maxopoly.caveworm.worms.Worm;
import org.bukkit.Location;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DistributionRunnable implements Runnable {

    private final Random rng;
    private final PseudoChunk[] chunks;
    private final double seedChance;
    private final Collection<IArea> exclusionAreas;
    private final int id;
    private final double upperYBound;
    private final double lowerYBound;
    private final int upperCaveLengthBound;
    private final int lowerCaveLengthBound;
    private final int minimumCaveLength;
    private final Caveworm plugin;
    private final WormConfig config;
    private final GlobalDistributor distributor;
    private final SimplexSphereFormer former;

    public DistributionRunnable(WormConfig config, int id, int seed,
                                PseudoChunk[] chunks, GlobalDistributor distributor) {
        this.seedChance = config.getDistributionSeedChance();
        this.config = config;
        this.rng = new Random(seed);
        this.chunks = chunks;
        this.exclusionAreas = config.getExclusionAreas();
        this.upperYBound = config.getUpperDistributionYBound();
        this.lowerYBound = config.getLowerDistributionYBound();
        this.plugin = Caveworm.getInstance();
        this.lowerCaveLengthBound = config
                .getLowerDistributionCaveLengthBound();
        this.upperCaveLengthBound = config
                .getUpperDistributionCaveLengthBound();
        this.minimumCaveLength = config.getMinimumDistributionCaveLength();
        this.distributor = distributor;
        this.former = CavewormAPI.getCaveFormer(config, id);
        this.id = id;
    }

    @Override
    public void run() {
        for (int currentIndex = 0; currentIndex < chunks.length; currentIndex++) {
            PseudoChunk c = chunks[currentIndex];
            if (c == null) {
                continue;
            }
            chunks[currentIndex] = null;
            for (IArea excluArea : exclusionAreas) {
                if (excluArea.isInArea(new Location(c.getWorld(),
                        c.getX() * 16, 0, (c.getZ() * 16) + 15))
                        || excluArea.isInArea(new Location(c.getWorld(), c
                        .getX() * 16, 0, c.getZ() * 16))
                        || excluArea.isInArea(new Location(c.getWorld(), (c
                        .getX() * 16) + 15, 0, c.getZ() * 16))
                        || excluArea.isInArea(new Location(c.getWorld(), (c
                        .getX() * 16) + 15, 0, (c.getZ() * 16) + 15))) {
                    return;
                }
            }
            double seedCopy = seedChance;
            while (seedCopy > 0) {
                if (rng.nextDouble() <= seedCopy) {
                    seedCopy--;
                    int x = rng.nextInt(16);
                    int z = rng.nextInt(16);
                    double yScaleFactor = rng.nextDouble();
                    double y = (int) (((upperYBound - lowerYBound) * yScaleFactor) + lowerYBound);
                    Location spawnLocation = new Location(c.getWorld(),
                            c.getX() * 16 + x, y, c.getZ() * 16 + z);
                    List<Location> spawnLocations = new LinkedList<Location>();
                    spawnLocations.add(spawnLocation);
                    List<LocationOffset> offSets = Caveworm.getWormConfig()
                            .getRandomLocationOffSet();
                    if (offSets != null) {
                        plugin.debug("Attempting to seed cave system "
                                + offSets + " around "
                                + spawnLocation);
                        for (LocationOffset offset : offSets) {
                            spawnLocations.add(offset
                                    .getOffSetLocation(spawnLocation));
                        }
                    }
                    for (Location loc : spawnLocations) {
                        double caveLengthFactor = rng.nextDouble();
                        int length = (int) (((upperCaveLengthBound - lowerCaveLengthBound) * caveLengthFactor) + lowerCaveLengthBound);
                        Worm w = CavewormAPI.getWorm(loc, length, config);
                        List<Location> path = w.getAllLocations();
                        if (path.size() < minimumCaveLength) {
                            plugin.debug("Attempted to spawn cave starting at "
                                    + loc.toString()
                                    + " but generated cave was only "
                                    + path.size()
                                    + " long, so it was not generated");
                            // not long enough;
                            continue;
                        }
                        plugin.debug("Seeding cave at " + loc.toString()
                                + " with a total length of " + path.size());
                        for (Location loca : path) {
                            former.extendLocation(loca);
                        }
                    }
                }
            }
            if ((currentIndex % 250) == 0) {
                Caveworm.getInstance().info(
                        currentIndex + " out of " + chunks.length
                                + " processed by thread " + id);
            }
        }
        former.clearRemaining();
        Caveworm.getInstance().info(
                "Thread " + id + " finished calculation");
        distributor.notifyCompletion(id);
    }

}
