package com.github.maxopoly.caveworm.distribution;

import com.github.maxopoly.caveworm.Caveworm;
import com.github.maxopoly.caveworm.WormConfig;
import com.github.maxopoly.caveworm.areas.IArea;
import com.github.maxopoly.caveworm.areas.PseudoChunk;

import java.util.Collection;
import java.util.Random;

public class GlobalDistributor {
    private final IArea area;
    private final Random rng;
    private final int threads;
    private final Caveworm plugin;
    private final WormConfig config;
    private final boolean[] threadTracking;

    public GlobalDistributor(WormConfig config, long seed, int threads) {
        this.config = config;
        this.rng = new Random(seed);
        this.plugin = Caveworm.getInstance();
        this.threads = threads;
        threadTracking = new boolean[threads];
        for (int i = 0; i < threadTracking.length; i++) {
            threadTracking[i] = false;
        }
        this.area = config.getDistributionArea();
    }

    public void distribute() {
        Collection<PseudoChunk> chunkCollection = area.getPseudoChunks();
        if (chunkCollection == null) {
            Caveworm.getInstance().warning(
                    "Couldnt distribute caves, chunk collection was null");
            return;
        }
        PseudoChunk[] allChunks = chunkCollection.toArray(new PseudoChunk[chunkCollection.size()]);
        plugin.info("Loaded " + allChunks.length + " chunks for generation");
        int chunksPerThread = (int) Math.ceil(((double) allChunks.length) / ((double) threads));
        for (int i = 0; i < threads; i++) {
            PseudoChunk[] chunkRun = new PseudoChunk[chunksPerThread];
            for (int x = 0; x < chunksPerThread && (i * chunksPerThread + x) < allChunks.length; x++) {
                chunkRun[x] = allChunks[i * chunksPerThread + x];
            }
            Thread t = new Thread(new DistributionRunnable(config, i, rng.nextInt(), chunkRun, this));
            t.start();
        }
    }

    synchronized void notifyCompletion(int id) {
        threadTracking[id] = true;
        for (int i = 0; i < threadTracking.length; i++) {
            if (!threadTracking[i]) {
                return;
            }
        }
        //all threads are done, so report completion to the job queue
        JobQueue jobs = JobQueue.getInstance();
        jobs.notifyCompletion(this);
    }
}
