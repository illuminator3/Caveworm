package com.github.maxopoly.caveworm.blockmodifications;

import com.github.maxopoly.caveworm.Caveworm;
import com.github.maxopoly.caveworm.CavewormAPI;
import com.github.maxopoly.caveworm.WormConfig;
import com.github.maxopoly.caveworm.caveformers.SimplexSphereFormer;
import com.github.maxopoly.caveworm.distribution.JobQueue;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FormingQueue implements Runnable {

    private final Queue<Location> pending;
    private final int modificationsPerTick;
    private final WormConfig config;
    private final SimplexSphereFormer former;
    private final JobQueue jobs;

    // private to avoid explicit instanciating
    public FormingQueue(WormConfig config, int modificationsPerTick) {
        this.pending = new ConcurrentLinkedQueue<Location>();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Caveworm.getInstance(),
                this, 1L, 1L);
        this.modificationsPerTick = modificationsPerTick;
        this.config = config;
        former = CavewormAPI.getCaveFormer(config, 0);
        this.jobs = JobQueue.getInstance();
    }

    public void add(Location modification) {
        pending.add(modification);
    }

    public void addAll(Collection<Location> locations) {
        pending.addAll(locations);
    }

    @Override
    public void run() {
        if (pending.size() == 0) {
            return;
        }
        int i = 0;
        while (!pending.isEmpty() && i < modificationsPerTick) {
            former.extendLocation(pending.remove());
            i++;
        }
        Caveworm.getInstance().info(
                "Finished expanding locations for this tick, currently left in queue: "
                        + pending.size() + "; Additional job not started yet: "
                        + jobs.getWaitingJobCount());
    }
}
