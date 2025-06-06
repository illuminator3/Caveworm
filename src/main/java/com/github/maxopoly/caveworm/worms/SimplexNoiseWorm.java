package com.github.maxopoly.caveworm.worms;

import org.bukkit.Location;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import java.util.*;

public class SimplexNoiseWorm extends Worm {

    private final Set<Location> currentPath;

    private final SimplexNoiseGenerator xNoiseGenerator;
    private final SimplexNoiseGenerator yNoiseGenerator;
    private final SimplexNoiseGenerator zNoiseGenerator;

    private int currentX;
    private int currentY;
    private int currentZ;
    private int currentLength;
    private boolean finished;

    private final int xMovementOctaves;
    private final int yMovementOctaves;
    private final int zMovementOctaves;

    private final double xSpreadFrequency;
    private final double ySpreadFrequency;
    private final double zSpreadFrequency;

    private final double xSpreadAmplitude;
    private final double ySpreadAmplitude;
    private final double zSpreadAmplitude;

    private final double xSpreadThreshHold;
    private final double ySpreadThreshHold;
    private final double zSpreadThreshHold;

    public SimplexNoiseWorm(Location startingLocation, int xMovementOctaves,
                            int yMovementOctaves, int zMovementOctaves,
                            double xSpreadFrequency, double ySpreadFrequency,
                            double zSpreadFrequency, double xSpreadThreshHold,
                            double ySpreadThreshHold, double zSpreadThreshHold,
                            double xSpreadAmplitude, double ySpreadAmplitude,
                            double zSpreadAmplitude, int maximumLength, long seedX, long seedY,
                            long seedZ) {
        super(startingLocation, maximumLength);
        this.xNoiseGenerator = new SimplexNoiseGenerator(seedX);
        this.yNoiseGenerator = new SimplexNoiseGenerator(seedY);
        this.zNoiseGenerator = new SimplexNoiseGenerator(seedZ);
        this.xMovementOctaves = xMovementOctaves;
        this.yMovementOctaves = yMovementOctaves;
        this.zMovementOctaves = zMovementOctaves;
        this.xSpreadFrequency = xSpreadFrequency;
        this.ySpreadFrequency = ySpreadFrequency;
        this.zSpreadFrequency = zSpreadFrequency;
        this.xSpreadThreshHold = xSpreadThreshHold;
        this.ySpreadThreshHold = ySpreadThreshHold;
        this.zSpreadThreshHold = zSpreadThreshHold;
        this.xSpreadAmplitude = xSpreadAmplitude;
        this.ySpreadAmplitude = ySpreadAmplitude;
        this.zSpreadAmplitude = zSpreadAmplitude;
        this.currentX = startingLocation.getBlockX();
        this.currentY = startingLocation.getBlockY();
        this.currentZ = startingLocation.getBlockZ();
        currentPath = new HashSet<Location>();
        this.currentLength = 0;
        finished = false;
    }

    public SimplexNoiseWorm(Location startingLocation, int xMovementOctaves,
                            int yMovementOctaves, int zMovementOctaves,
                            double xSpreadFrequency, double ySpreadFrequency,
                            double zSpreadFrequency, double xSpreadThreshHold,
                            double ySpreadThreshHold, double zSpreadThreshHold,
                            double xSpreadAmplitude, double ySpreadAmplitude,
                            double zSpreadAmplitude, int maximumLength) {
        this(startingLocation, xMovementOctaves, yMovementOctaves,
                zMovementOctaves, xSpreadFrequency, ySpreadFrequency,
                zSpreadFrequency, xSpreadThreshHold, ySpreadThreshHold,
                zSpreadThreshHold, xSpreadAmplitude, ySpreadAmplitude,
                zSpreadAmplitude, maximumLength, new Random().nextLong(),
                new Random().nextLong(), new Random().nextLong());
    }

    public boolean hasNext() {
        return !finished;
    }

    public Location next() {
        if (finished) {
            return null;
        }
        Location result = new Location(startingLocation.getWorld(), currentX,
                currentY, currentZ);
        // precalculate next result

        // move in x direction
        double x = xNoiseGenerator.noise(currentX, currentY, currentZ,
                xMovementOctaves, xSpreadFrequency, xSpreadAmplitude, true);
        if (x >= xSpreadThreshHold) {
            currentX++;
        }
        if (x <= (-1 * xSpreadThreshHold)) {
            currentX--;
        }

        // move in y direction
        double y = yNoiseGenerator.noise(currentX, currentY, currentZ,
                yMovementOctaves, ySpreadFrequency, ySpreadAmplitude, true);
        if (y >= ySpreadThreshHold) {
            currentY++;
        }
        if (y <= (-1 * ySpreadThreshHold)) {
            currentY--;
        }

        // move in z direction
        double z = zNoiseGenerator.noise(currentX, currentY, currentZ,
                zMovementOctaves, zSpreadFrequency, zSpreadAmplitude, true);
        if (z >= zSpreadThreshHold) {
            currentZ++;
        }
        if (z <= (-1 * zSpreadThreshHold)) {
            currentZ--;
        }
        // increment length counter
        currentLength++;
        //if maximum length was reached, stop
        if (currentLength >= maximumLength) {
            finished = true;
        }
        // if this point was already reached, we are inside a cycle and can stop
        // earlier
        Location nextOne = new Location(startingLocation.getWorld(), currentX,
                currentY, currentZ);
        if (currentPath.contains(nextOne)) {
            finished = true;
        } else {
            currentPath.add(nextOne);
        }
        return result;
    }

    public List<Location> getAllLocations()
            throws ConcurrentModificationException {
        if (currentX != startingLocation.getBlockX()
                || currentY != startingLocation.getBlockY()
                || currentZ != startingLocation.getBlockZ()) {
            throw new ConcurrentModificationException();
        }
        List<Location> result = new ArrayList<Location>();
        while (hasNext()) {
            result.add(next());
        }
        return result;
    }

    public int getCurrentLength() {
        return currentLength;
    }

}
