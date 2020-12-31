package com.github.hornta.trollskogen_core;

import com.github.hornta.trollskogen_core.events.LinkRequestEvent;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.trollskogen_core.users.events.LinkUserEvent;
import com.github.hornta.trollskogen_core.users.events.LoadUsersEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DiscordListener implements EventListener, Listener {
	private final InheritanceNode linkedNode;
	private JDA jda;
	private boolean isReady;

	public DiscordListener() {
		linkedNode = InheritanceNode.builder("linked").build();
	}

	private void handleUnlinkAccount(MessageReceivedEvent event) {
		var user = TrollskogenCorePlugin.getUserManager().getUser(event.getAuthor());
		if (user == null) {
			event.getMessage().reply("You are not linked to a Minecraft account.").submit();
			return;
		}

		for (var guild : jda.getGuilds()) {
			if (guild.getId().equals(TrollskogenCorePlugin.getConfiguration().<String>get(ConfigKey.DISCORD_GUILD_ID))) {
				var member = guild.getMemberById(user.getDiscordUserId());
				if (member != null && guild.getSelfMember().canInteract(member)) {
					guild.modifyMemberRoles(member, Collections.emptyList()).submit();
				}
			}
		}

		removeLinkFromUser(user);

		event.getMessage().reply("You have now removed the link between your Discord account and Minecraft account.").submit();
	}

	private void handleLinkAccount(MessageReceivedEvent event, String[] args) {
		var user = TrollskogenCorePlugin.getUserManager().getUser(event.getAuthor());
		if (user != null && user.getDiscordUserId() != null) {
			event.getMessage().reply("Your Discord account is already linked to your Minecraft account.").submit();
			return;
		}

		var playerName = args[0];
		var player = Bukkit.getPlayer(playerName);
		if (player == null || !player.isOnline()) {
			event.getMessage().reply("This player is not logged into the Minecraft server.").submit();
		} else {
			var userObject = TrollskogenCorePlugin.getUserManager().getUser(playerName);
			if (userObject == null) {
				event.getMessage().reply("Player not found.").submit();
			} else if (userObject.getDiscordUserId() != null) {
				event.getMessage().reply("This account is already linked.").submit();
			} else {
				var linkRequest = new LinkRequest(userObject, event.getAuthor());
				Bukkit.getScheduler().callSyncMethod(TrollskogenCorePlugin.getInstance(), () -> {
					Bukkit.getPluginManager().callEvent(new LinkRequestEvent(linkRequest));
					return true;
				});
				event.getMessage().reply("A link request has been sent to you in the Minecraft server chat. Please accept it to successfully link your account.").submit();
			}
		}
	}

	@Override
	public void onEvent(@NotNull GenericEvent event) {
		if (event instanceof MessageReceivedEvent) {
			handleMessageReceived((MessageReceivedEvent) event);
		} else if (event instanceof ReadyEvent) {
			handleReady((ReadyEvent) event);
		}
	}

	void handleReady(ReadyEvent event) {
		jda = event.getJDA();
		isReady = true;

		adjustNicknamesAndRoles();
	}

	private void adjustNicknamesAndRoles() {
		var userManager = TrollskogenCorePlugin.getUserManager();
		var hasAllUsers = userManager.hasLoadedAllUsers();

		if (isReady && hasAllUsers) {
			var luckPerms = TrollskogenCorePlugin.getLuckPerms();
			var rolesToUsers = new ConcurrentHashMap<Member, Collection<Role>>();
			var futures = new ArrayList<CompletableFuture<Void>>();

			for (var guild : jda.getGuilds()) {
				if (guild.getId().equals(TrollskogenCorePlugin.getConfiguration().<String>get(ConfigKey.DISCORD_GUILD_ID))) {
					for (var member : guild.getMembers()) {
						if (guild.getSelfMember().canInteract(member)) {
							var userObject = TrollskogenCorePlugin.getUserManager().getUser(member.getUser());
							if (userObject != null) {
								futures.add(luckPerms.getUserManager().loadUser(userObject.getUuid()).thenAcceptAsync((User lpUser) -> {
									lpUser.data().add(linkedNode);
									rolesToUsers.put(member, getRolesFromLuckPermGroups(member, lpUser));
								}));
								member.modifyNickname(userObject.getName()).submit();
							} else {
								if (member.getRoles().size() != 0) {
									rolesToUsers.put(member, new HashSet<>());
								}
							}
						}
					}
				}
			}

			futures.forEach(CompletableFuture::join);

			for (var roles : rolesToUsers.entrySet()) {
				roles.getKey().getGuild().modifyMemberRoles(roles.getKey(), roles.getValue()).submit();
			}

			for (var user : userManager.getUsers()) {
				if (user.getDiscordUserId() != null) {
					var discordUser = jda.getUserById(user.getDiscordUserId());
					if (discordUser == null) {
						removeLinkFromUser(user);
					} else {
						for (var guild : jda.getGuilds()) {
							if (guild.getId().equals(TrollskogenCorePlugin.getConfiguration().<String>get(ConfigKey.DISCORD_GUILD_ID)) && !guild.isMember(discordUser)) {
								removeLinkFromUser(user);
							}
						}
					}
				}
			}
		}
	}

	private Collection<Role> getRolesFromLuckPermGroups(Member member, User lpUser) {
		var roles = new HashSet<Role>();
		for (var group : lpUser.getInheritedGroups(lpUser.getQueryOptions())) {
			var metaDiscordRole = group.getCachedData().getMetaData().getMetaValue("discord_role");
			if (metaDiscordRole != null) {
				roles.addAll(member.getGuild().getRolesByName(metaDiscordRole, true));
			}
		}
		return roles;
	}

	private void removeLinkFromUser(UserObject user) {
		var luckPerms = TrollskogenCorePlugin.getLuckPerms();
		luckPerms.getUserManager().loadUser(user.getUuid()).thenAcceptAsync((User lpUser) -> {
			lpUser.data().remove(linkedNode);
		});
		user.setDiscordUserId(null);
		TrollskogenCorePlugin.getUserManager().updateUser(user);
	}

	@EventHandler
	void onLinkedUser(LinkUserEvent event) {
		var luckPerms = TrollskogenCorePlugin.getLuckPerms();
		var discordUser = jda.getUserById(event.getUserObject().getDiscordUserId());
		for (var guild : jda.getGuilds()) {
			if (guild.getId().equals(TrollskogenCorePlugin.getConfiguration().<String>get(ConfigKey.DISCORD_GUILD_ID)) && discordUser != null && guild.isMember(discordUser)) {
				luckPerms.getUserManager().loadUser(event.getUserObject().getUuid()).thenAcceptAsync((User lpUser) -> {
					lpUser.data().add(linkedNode);
					var member = guild.getMember(discordUser);
					if (member != null && guild.getSelfMember().canInteract(member)) {
						member.modifyNickname(event.getUserObject().getName()).submit();
						guild.modifyMemberRoles(member, getRolesFromLuckPermGroups(member, lpUser)).submit();
						discordUser.openPrivateChannel().queue((var channel) -> {
							channel.sendMessage("Thank you for linking your Discord account to your Minecraft account.\nYou can now participate in races, earn in game currency just by playing and use /tpa.").submit();
						});
					}
				});
			}
		}
	}

	@EventHandler
	void onLoadUsers(LoadUsersEvent event) {
		adjustNicknamesAndRoles();
	}

	private void displaySyntaxError(MessageReceivedEvent event) {
		event.getMessage().reply("**Unrecognized command!**\nCurrently these are the commands you can perform:\n```link <player_name> - Link your Discord account with your Minecraft account.\nunlink - Removes the link between your Discord account and Minecraft account.```").submit();
	}

	private void handleMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot() || event.getChannelType() != ChannelType.PRIVATE) {
			return;
		}
		var parts = event.getMessage().getContentRaw().trim().split(" ");
		if (parts.length == 0) {
			displaySyntaxError(event);
		} else {
			if (parts[0].equalsIgnoreCase("link") && parts.length == 2) {
				handleLinkAccount(event, Arrays.copyOfRange(parts, 1, parts.length));
			} else if (parts[0].equalsIgnoreCase("unlink") && parts.length == 1) {
				handleUnlinkAccount(event);
			} else {
				displaySyntaxError(event);
			}
		}
	}
}
