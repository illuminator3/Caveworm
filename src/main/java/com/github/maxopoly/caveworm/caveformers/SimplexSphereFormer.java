package com.github.maxopoly.caveworm.caveformers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SimplexSphereFormer implements CaveFormer {

    private final SimplexNoiseGenerator xGenerator;
    private final SimplexNoiseGenerator yGenerator;
    private final SimplexNoiseGenerator zGenerator;

    private final Material replacementMaterial;
    private final double amplitude;
    private final Collection<Material> ignoreMaterials;

    private final int xOctaves;
    private final int yOctaves;
    private final int zOctaves;

    private final double xSpreadFrequency;
    private final double ySpreadFrequency;
    private final double zSpreadFrequency;

    private final double xUpperRadiusBound;
    private final double yUpperRadiusBound;
    private final double zUpperRadiusBound;

    private final double xLowerRadiusBound;
    private final double yLowerRadiusBound;
    private final double zLowerRadiusBound;

    private final int xzSlices;
    private final int xySlices;
    private final int yzSlices;

    private final FallingBlockHandler fallingBlockHandler;

    private final List<Location> doLater;
    private List<Location> doLaterCenters;

    private final int id;

    public SimplexSphereFormer(int id, Material replacementMaterial, int xOctaves,
                               int yOctaves, int zOctaves, double xSpreadFrequency,
                               double ySpreadFrequency, double zSpreadFrequency,
                               double xUpperRadiusBound, double yUpperRadiusBound,
                               double zUpperRadiusBound, double xLowerRadiusBound,
                               double yLowerRadiusBound, double zLowerRadiusBound, int xzSlices,
                               int xySlices, int yzSlices, Collection<Material> materialsToIgnore,
                               int fallingBlockBehavior, Material fallingBlockReplacement,
                               long xSeed, long ySeed, long zSeed) {
        this.replacementMaterial = replacementMaterial;
        this.amplitude = 2.0; // hardcoded to ensure it properly scales with the
        // bounds
        this.id = id;
        this.xGenerator = new SimplexNoiseGenerator(xSeed);
        this.yGenerator = new SimplexNoiseGenerator(ySeed);
        this.zGenerator = new SimplexNoiseGenerator(zSeed);
        this.xOctaves = xOctaves;
        this.yOctaves = yOctaves;
        this.zOctaves = zOctaves;
        this.xSpreadFrequency = xSpreadFrequency;
        this.ySpreadFrequency = ySpreadFrequency;
        this.zSpreadFrequency = zSpreadFrequency;
        this.xUpperRadiusBound = xUpperRadiusBound;
        this.yUpperRadiusBound = yUpperRadiusBound;
        this.zUpperRadiusBound = zUpperRadiusBound;
        this.xLowerRadiusBound = xLowerRadiusBound;
        this.yLowerRadiusBound = yLowerRadiusBound;
        this.zLowerRadiusBound = zLowerRadiusBound;
        this.xySlices = xySlices;
        this.xzSlices = xzSlices;
        this.yzSlices = yzSlices;
        this.doLater = new LinkedList<>();
        this.ignoreMaterials = materialsToIgnore;
        switch (fallingBlockBehavior) {
            case 0:
                // ignore the fact that there's a falling block and clear the block
                // below anyway
                fallingBlockHandler = (b) -> {
                    executeBlockModification(b);
                };
                break;
            case 1:
                // dont clear the block
                fallingBlockHandler = (b) -> {
                };
                break;
            case 2:
                // move the block one upwards instead of clearing it and if the
                // block moved is gravity affected as well, instead another block is
                // put in place
                fallingBlockHandler = (b) -> {
                    Block above = b.getRelative(BlockFace.UP);
                    if (b.getType().hasGravity()) {
                        above.setType(fallingBlockReplacement);
                        executeBlockModification(b);
                    } else {
                        above.setType(b.getType(), false);
                        above.setBlockData(b.getBlockData(), false);
                        executeBlockModification(b);
                    }
                };
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public SimplexSphereFormer(int id, Material replacementMaterial, int xOctaves,
                               int yOctaves, int zOctaves, double xSpreadFrequency,
                               double ySpreadFrequency, double zSpreadFrequency,
                               double xUpperRadiusBound, double yUpperRadiusBound,
                               double zUpperRadiusBound, double xLowerRadiusBound,
                               double yLowerRadiusBound, double zLowerRadiusBound, int xzSlices,
                               int xySlices, int yzSlices, Collection<Material> materialsToIgnore,
                               int fallingBlockBehavior, Material fallingBlockReplacement) {
        this(id, replacementMaterial, xOctaves,
                yOctaves, zOctaves, xSpreadFrequency, ySpreadFrequency,
                zSpreadFrequency, xUpperRadiusBound, yUpperRadiusBound,
                zUpperRadiusBound, xLowerRadiusBound, yLowerRadiusBound,
                zLowerRadiusBound, xzSlices, xySlices, yzSlices,
                materialsToIgnore, fallingBlockBehavior,
                fallingBlockReplacement, new Random().nextLong(), new Random()
                        .nextLong(), new Random().nextLong());
    }

    public void clearRemaining() {
        int failCounter = 0;
        while (doLater.size() != 0) {
            int startSize = doLater.size();
            // copy in new array to avoid concurrent problems when forming fails
            // while doing this
            Location[] locsToDoNow = doLater.toArray(new Location[startSize]);
            doLater.clear();
            for (Location loc : locsToDoNow) {
                clearBlock(loc);
            }
            System.out.println("Cleanupqueue for thread " + id + " processed "
                    + (startSize - doLater.size()) + " locations, "
                    + doLater.size() + " left ");
            if (startSize == doLater.size()) {
                failCounter++;
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                failCounter = 0;
            }
            if (failCounter >= 1000) {
                return;
            }
        }
    }

    public void extendLocation(Location loc) {
        // always clear center block
        clearBlock(loc);
        // calculate x distance blocks must be within
        double currentXRange = xGenerator.noise(loc.getX(), loc.getY(),
                loc.getZ(), xOctaves, xSpreadFrequency, amplitude, true);
        currentXRange = (Math.abs(currentXRange
                * (xUpperRadiusBound - xLowerRadiusBound)))
                + xLowerRadiusBound;
        // calculate y distance blocks must be within
        double currentYRange = yGenerator.noise(loc.getX(), loc.getY(),
                loc.getZ(), yOctaves, ySpreadFrequency, amplitude, true);
        currentYRange = (Math.abs(currentYRange
                * (yUpperRadiusBound - yLowerRadiusBound)))
                + yLowerRadiusBound;
        // calculate z distance blocks must be within
        double currentZRange = zGenerator.noise(loc.getX(), loc.getY(),
                loc.getZ(), zOctaves, zSpreadFrequency, amplitude, true);
        currentZRange = (Math.abs(currentZRange
                * (zUpperRadiusBound - zLowerRadiusBound)))
                + zLowerRadiusBound;
        // clear circle in X-Z direction
        for (int yOffSet = 0; yOffSet <= xzSlices; yOffSet++) {
            for (double relX = loc.getX() - xUpperRadiusBound; relX <= loc
                    .getX() + xUpperRadiusBound; relX++) {
                for (double relZ = loc.getZ() - zUpperRadiusBound; relZ <= loc
                        .getZ() + zUpperRadiusBound; relZ++) {
                    Location temp = new Location(loc.getWorld(), relX,
                            loc.getY() + yOffSet, relZ);
                    double xDistance = loc.getX() - temp.getX();
                    double zDistance = loc.getZ() - temp.getZ();
                    // check whether point is inside ellipse
                    if (((xDistance * xDistance) / (currentXRange * currentXRange))
                            + ((yOffSet * yOffSet) / (currentYRange * currentYRange))
                            + ((zDistance * zDistance) / (currentZRange * currentZRange)) <= 1) {
                        clearBlock(temp);
                        clearBlock(new Location(loc.getWorld(), relX,
                                loc.getY() - yOffSet, relZ));
                    }
                }
            }
        }
        // clear circle in Y-Z direction
        for (int xOffSet = 0; xOffSet <= yzSlices; xOffSet++) {
            for (double relY = loc.getY() - yUpperRadiusBound; relY <= loc
                    .getY() + yUpperRadiusBound; relY++) {
                for (double relZ = loc.getZ() - zUpperRadiusBound; relZ <= loc
                        .getZ() + zUpperRadiusBound; relZ++) {
                    Location temp = new Location(loc.getWorld(), loc.getX()
                            + xOffSet, relY, relZ);
                    double yDistance = loc.getY() - temp.getY();
                    double zDistance = loc.getZ() - temp.getZ();
                    // check whether point is inside ellipse
                    if (((xOffSet * xOffSet) / (currentXRange * currentXRange))
                            + ((yDistance * yDistance) / (currentYRange * currentYRange))
                            + ((zDistance * zDistance) / (currentZRange * currentZRange)) <= 1) {
                        clearBlock(temp);
                        clearBlock(new Location(loc.getWorld(), loc.getX()
                                - xOffSet, relY, relZ));
                    }
                }
            }
        }
        // clear circle in X-Y direction
        for (int zOffSet = 0; zOffSet <= xySlices; zOffSet++) {
            for (double relX = loc.getX() - xUpperRadiusBound; relX <= loc
                    .getX() + xUpperRadiusBound; relX++) {
                for (double relY = loc.getY() - yUpperRadiusBound; relY <= loc
                        .getY() + yUpperRadiusBound; relY++) {
                    Location temp = new Location(loc.getWorld(), relX, relY,
                            loc.getZ() + zOffSet);
                    double yDistance = loc.getY() - temp.getY();
                    double xDistance = loc.getX() - temp.getX();
                    // check whether point is inside ellipse
                    if (((xDistance * xDistance) / (currentXRange * currentXRange))
                            + ((yDistance * yDistance) / (currentYRange * currentYRange))
                            + ((zOffSet * zOffSet) / (currentZRange * currentZRange)) <= 1) {
                        clearBlock(temp);
                        clearBlock(new Location(loc.getWorld(), relX, relY,
                                loc.getZ() - zOffSet));
                    }
                }
            }

        }
    }

    private void executeBlockModification(Block b) {
        b.setType(replacementMaterial, false);
        Block comp = b.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ());
        if (!comp.getType().equals(replacementMaterial)) {
            doLater.add(b.getLocation());
        }
    }

    private void clearBlock(Location loc) {
        if (loc.getBlockY() <= 0 || loc.getBlockY() > 255) {
            return;
        }
        Block b = loc.getBlock();
        if ((!ignoreMaterials.contains(b.getType()))
                && (b.getType() != replacementMaterial)) {
            if (b.getRelative(BlockFace.UP).getType().hasGravity()) {
                fallingBlockHandler.handle(b);
            } else {
                executeBlockModification(b);
            }
        }
    }

    private interface FallingBlockHandler {
        void handle(Block b);
    }
}
