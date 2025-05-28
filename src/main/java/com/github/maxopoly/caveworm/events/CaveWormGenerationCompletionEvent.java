package com.github.maxopoly.caveworm.events;

import com.github.maxopoly.caveworm.WormConfig;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CaveWormGenerationCompletionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final WormConfig configUsed;

    public CaveWormGenerationCompletionEvent(WormConfig config) {
        this.configUsed = config;
    }

    public WormConfig getConfig() {
        return configUsed;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
