package com.github.maxopoly.caveworm.worms;

import org.bukkit.Location;

import java.util.Iterator;
import java.util.List;

public abstract class Worm implements Iterator<Location> {

    protected Location startingLocation;
    protected int maximumLength;

    public Worm(Location startingLocation, int maximumLength) {
        this.startingLocation = startingLocation;
        this.maximumLength = maximumLength;
    }

    public abstract List<Location> getAllLocations();
}
