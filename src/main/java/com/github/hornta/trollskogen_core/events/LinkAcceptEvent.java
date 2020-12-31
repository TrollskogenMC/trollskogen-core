package com.github.hornta.trollskogen_core.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LinkAcceptEvent extends Event {
	private static final HandlerList HANDLERS = new HandlerList();
	private final Player player;

	public LinkAcceptEvent(Player player) {
		this.player = player;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
}
