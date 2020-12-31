package com.github.hornta.trollskogen_core.events;

import com.github.hornta.trollskogen_core.LinkRequest;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LinkRequestEvent extends Event {
	private static final HandlerList HANDLERS = new HandlerList();
	private final LinkRequest linkRequest;

	public LinkRequestEvent(LinkRequest linkRequest) {
		this.linkRequest = linkRequest;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public LinkRequest getLinkRequest() {
		return linkRequest;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
}
