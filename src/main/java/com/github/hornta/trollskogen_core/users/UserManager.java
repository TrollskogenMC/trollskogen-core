package com.github.hornta.trollskogen_core.users;

import com.github.hornta.messenger.MessageManager;
import com.github.hornta.trollskogen_core.DateUtils;
import com.github.hornta.trollskogen_core.MessageKey;
import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
import com.github.hornta.trollskogen_core.users.deserializers.PatchedUserObjectDeserializer;
import com.github.hornta.trollskogen_core.users.deserializers.PostedUserObjectDeserializer;
import com.github.hornta.trollskogen_core.users.deserializers.UserObjectDeserializer;
import com.github.hornta.trollskogen_core.users.events.LoadUsersEvent;
import com.github.hornta.trollskogen_core.users.events.NewUserEvent;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.dv8tion.jda.api.entities.User;
import org.asynchttpclient.Response;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.logging.Level;

public class UserManager implements Listener {
	public static DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withLocale(Locale.UK).withZone(ZoneId.systemDefault());
	private final TrollskogenCorePlugin main;
	private final List<UserObject> users = new ArrayList<>();
	private final Map<UUID, UserObject> uuidToUser = new HashMap<>();
	private final Map<String, UserObject> nameToUser = new HashMap<>();
	private final Map<Integer, UserObject> idToUser = new HashMap<>();
	private final Map<String, UserObject> discordIdToUser = new HashMap<>();
	private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
	private final Set<UUID> pendingAddUser = new HashSet<>();
	private boolean hasLoadedAllUsers;

	public UserManager(TrollskogenCorePlugin main) {
		this.main = main;
	}

	public static UserObject parseUser(JsonObject json) {
		Instant lastJoinDate = null;
		if (!json.get("last_join_date").isJsonNull()) {
			lastJoinDate = Instant.parse(json.get("last_join_date").getAsString());
		}
		String discordUserId = null;
		if (!json.get("discord_user_id").isJsonNull()) {
			discordUserId = json.get("discord_user_id").getAsString();
		}
		return new UserObject(
			json.get("id").getAsInt(),
			json.get("name").getAsString(),
			UUID.fromString(json.get("minecraft_uuid").getAsString()),
			discordUserId,
			lastJoinDate
		);
	}

	public UserObject getUser(Player player) {
		if (!uuidToUser.containsKey(player.getUniqueId())) {
			postUser(player);
			return null;
		}
		return uuidToUser.get(player.getUniqueId());
	}

	public UserObject getUser(UUID uuid) {
		return uuidToUser.get(uuid);
	}

	public UserObject getUser(String name) {
		return nameToUser.get(name);
	}

	public UserObject getUser(int id) {
		return idToUser.get(id);
	}

	public UserObject getUser(User user) {
		return discordIdToUser.get(user.getId());
	}

	public List<UserObject> getUsers() {
		return users;
	}

	public boolean hasLoadedAllUsers() {
		return hasLoadedAllUsers;
	}

	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event) {
		main.scheduleSyncDelayedTask(() -> {
			if (!event.getPlayer().hasPlayedBefore()) {
				event.getPlayer().saveData();
				MessageManager.setValue("player", event.getPlayer().getName());
				MessageManager.broadcast(MessageKey.FIRST_JOIN_MESSAGE);
			}
		});
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent e) {
		if (!main.getBanManager().hasLoadedBans() || !hasLoadedAllUsers || !TrollskogenCorePlugin.getServerReady().isReady()) {
			e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server not ready yet.");
			return;
		}

		if (!uuidToUser.containsKey(e.getPlayer().getUniqueId())) {
			postUser(e.getPlayer());
		} else {
			var user = getUser(e.getPlayer());
			user.setName(e.getPlayer().getName());
			user.setUuid(e.getPlayer().getUniqueId());
			updateUser(user);
			var ban = main.getBanManager().getLongestBan(user);
			if (ban != null) {
				MessageManager.setValue("reason", ban.getReason());
				if (ban.getExpiryDate() == null) {
					e.disallow(PlayerLoginEvent.Result.KICK_BANNED, MessageManager.getMessage(MessageKey.KICKBAN_PERMANENT));
				} else {
					MessageManager.setValue("time_left", DateUtils.formatDateDiff(LocalDateTime.ofInstant(ban.getExpiryDate(), ZoneId.of("Europe/Stockholm"))));
					e.disallow(PlayerLoginEvent.Result.KICK_BANNED, MessageManager.getMessage(MessageKey.KICKBAN_TEMPORARY));
				}
				return;
			}

			if (main.isMaintenance() && !main.isAllowedMaintenance(e.getPlayer())) {
				e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "Server is under maintenance.");
			}
		}
	}

	private void postUser(Player player) {
		if (pendingAddUser.contains(player.getUniqueId())) {
			return;
		}
		pendingAddUser.add(player.getUniqueId());

		var json = new JsonObject();
		json.addProperty("minecraft_uuid", player.getUniqueId().toString());
		json.addProperty("name", player.getName());
		json.addProperty("last_join_date", formatter.format(Instant.now()));

		scheduledExecutor.submit(() -> {
			TrollskogenCorePlugin.request("POST", "/user", json, (Response response) -> {
				var gson = new GsonBuilder()
					.registerTypeAdapter(UserObject.class, new PostedUserObjectDeserializer())
					.create();
				UserObject user;
				try {
					user = gson.fromJson(response.getResponseBody(), UserObject.class);
				} catch (JsonSyntaxException ex) {
					Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
					return;
				}
				Bukkit.getScheduler().callSyncMethod(main, () -> {
					users.add(user);
					uuidToUser.put(user.getUuid(), user);
					nameToUser.put(user.getName(), user);
					idToUser.put(user.getId(), user);
					Bukkit.getPluginManager().callEvent(new NewUserEvent(user));
					pendingAddUser.remove(user.getUuid());
					return null;
				});
			});
		});
	}

	public void updateUser(UserObject user) {
		updateUser(user, null);
	}

	public void updateUser(UserObject user, Consumer<Response> callback) {
		var json = new JsonObject();
		json.addProperty("discord_user_id", user.getDiscordUserId());
		json.addProperty("minecraft_uuid", user.getUuid().toString());
		json.addProperty("name", user.getName());
		json.addProperty("last_join_date", formatter.format(Instant.now()));

		scheduledExecutor.submit(() -> {
			TrollskogenCorePlugin.request("PATCH", "/user/" + user.getId(), json, (Response response) -> {
				var gson = new GsonBuilder()
					.registerTypeAdapter(UserObject.class, new PatchedUserObjectDeserializer())
					.create();
				UserObject parsedUser;
				try {
					parsedUser = gson.fromJson(response.getResponseBody(), UserObject.class);
				} catch (Throwable ex) {
					Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
					return;
				}
				Bukkit.getScheduler().callSyncMethod(main, () -> {
					var prevName = user.getName();

					user.setDiscordUserId(parsedUser.getDiscordUserId());
					user.setLastJoinDate(parsedUser.getLastJoinDate());
					user.setName(parsedUser.getName());
					user.setUuid(parsedUser.getUuid());

					if (!prevName.equals(parsedUser.getName()) && user.getUuid() == parsedUser.getUuid()) {
						nameToUser.remove(prevName);
						nameToUser.put(user.getName(), user);
					}

					if (user.getDiscordUserId() != null) {
						discordIdToUser.put(user.getDiscordUserId(), user);
					}

					if (callback != null) {
						callback.accept(response);
					}
					return null;
				});
			});
		});
	}

	public void loadAllUsers() {
		hasLoadedAllUsers = false;
		scheduledExecutor.submit(() -> {
			TrollskogenCorePlugin.request("GET", "/users", (Response response) -> {
				var gson = new GsonBuilder()
					.registerTypeAdapter(UserObject[].class, new UserObjectDeserializer())
					.create();
				UserObject[] parsedUsers;
				try {
					parsedUsers = gson.fromJson(response.getResponseBody(), UserObject[].class);
				} catch (Throwable ex) {
					Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
					return;
				}

				Bukkit.getScheduler().callSyncMethod(main, () -> {
					users.clear();
					uuidToUser.clear();
					nameToUser.clear();
					idToUser.clear();
					discordIdToUser.clear();
					for (var userObject : parsedUsers) {
						users.add(userObject);
						uuidToUser.put(userObject.getUuid(), userObject);
						nameToUser.put(userObject.getName(), userObject);
						idToUser.put(userObject.getId(), userObject);
						if (userObject.getDiscordUserId() != null) {
							discordIdToUser.put(userObject.getDiscordUserId(), userObject);
						}
					}
					hasLoadedAllUsers = true;
					Bukkit.getLogger().info("Loaded " + parsedUsers.length + " users");
					Bukkit.getPluginManager().callEvent(new LoadUsersEvent());
					return null;
				});
			});
		});
	}
}
