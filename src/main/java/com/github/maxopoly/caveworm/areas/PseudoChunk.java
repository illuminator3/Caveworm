package com.github.maxopoly.caveworm.areas;

import org.bukkit.Chunk;
import org.bukkit.World;

public class PseudoChunk {

    private final World world;

    private final int x;

    private final int z;

    public PseudoChunk(World w, int x, int z) {
        this.world = w;
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public World getWorld() {
        return world;
    }

    public int getActualX() {
        return x * 16;
    }

    public int getActualZ() {
        return z * 16;
    }

    public Chunk getActualChunk() {
        return world.getChunkAt(x, z);
    }

}