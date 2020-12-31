package com.github.hornta.trollskogen_core;

import com.github.hornta.trollskogen_core.events.LinkAcceptEvent;
import com.github.hornta.trollskogen_core.events.LinkDeclineEvent;
import com.github.hornta.trollskogen_core.events.LinkRequestEvent;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_core.users.events.LinkUserEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.asynchttpclient.Response;
import org.asynchttpclient.util.HttpConstants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class LinkManager implements Listener {
	private final Map<UserObject, LinkRequest> currentRequests;
	private final Map<LinkRequest, BukkitRunnable> expirationTasks;

	public LinkManager() {
		currentRequests = new HashMap<>();
		expirationTasks = new HashMap<>();
	}

	@EventHandler
	void onLinkAccept(LinkAcceptEvent event) {
		var userObject = TrollskogenCorePlugin.getUserManager().getUser(event.getPlayer());
		var linkRequest = currentRequests.get(userObject);
		if (linkRequest != null) {
			userObject.setDiscordUserId(linkRequest.getDiscordUser().getId());
			TrollskogenCorePlugin.getUserManager().updateUser(userObject, (Response response) -> {
				if (response.getStatusCode() != HttpConstants.ResponseStatusCodes.OK_200) {
					event.getPlayer().sendMessage("§cSomething failed when linking your account. Please try again later");
				} else {
					var linkEvent = new LinkUserEvent(userObject);
					Bukkit.getPluginManager().callEvent(linkEvent);
					Bukkit.broadcastMessage("§a" + userObject.getName() + " has linked their Discord account with their Minecraft account.");
				}
			});
			removeLinkRequest(linkRequest);
		} else {
			displayNoPendingLinkRequests(event.getPlayer());
		}
	}

	@EventHandler
	void onLinkDecline(LinkDeclineEvent event) {
		var userObject = TrollskogenCorePlugin.getUserManager().getUser(event.getPlayer());
		var linkRequest = currentRequests.get(userObject);
		if (linkRequest != null) {
			removeLinkRequest(linkRequest);
			event.getPlayer().sendMessage("§aYou have §cdeclined §athe link request.");
		} else {
			displayNoPendingLinkRequests(event.getPlayer());
		}
	}

	private void displayNoPendingLinkRequests(Player player) {
		player.sendMessage("§cYou don´t have a pending link request.");
	}

	private void removeLinkRequest(LinkRequest linkRequest) {
		currentRequests.remove(linkRequest.getUser());
		if (expirationTasks.containsKey(linkRequest)) {
			expirationTasks.get(linkRequest).cancel();
		}
		expirationTasks.remove(linkRequest);
	}

	@EventHandler
	void onLinkRequest(LinkRequestEvent event) {
		currentRequests.put(event.getLinkRequest().getUser(), event.getLinkRequest());
		var baseMessage = new TextComponent("§7You got a request to link your Discord account with this Minecraft account. Please §aaccept §7or §cdecline §7it. You §7have 20 seconds to take an action.\n§7Discord user: §a" + event.getLinkRequest().getDiscordUser().getAsTag() + "\n");
		var acceptClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ts link accept");
		var declineClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ts link decline");
		var acceptMessage = new TextComponent("§a§lAccept");
		var declineMessage = new TextComponent("§c§lDecline");
		acceptMessage.setClickEvent(acceptClick);
		declineMessage.setClickEvent(declineClick);
		baseMessage.addExtra(acceptMessage);
		baseMessage.addExtra(" §7| ");
		baseMessage.addExtra(declineMessage);
		event.getLinkRequest().getUser().getPlayer().spigot().sendMessage(baseMessage);
		var task = new ExpirationTask(event.getLinkRequest());
		var TWENTY_SECONDS = 20 * 20;
		expirationTasks.put(event.getLinkRequest(), task);
		task.runTaskLater(TrollskogenCorePlugin.getInstance(), TWENTY_SECONDS);
	}

	private class ExpirationTask extends BukkitRunnable {
		private final LinkRequest linkRequest;

		ExpirationTask(LinkRequest linkRequest) {
			this.linkRequest = linkRequest;
		}

		@Override
		public void run() {
			removeLinkRequest(linkRequest);

			var player = Bukkit.getPlayer(linkRequest.getUser().getUuid());
			if (player != null && player.isOnline()) {
				player.sendMessage("§cYour link requests has been canceled due to the time limit.");
			}
		}
	}
}
