package com.github.hornta.trollskogen_core;

import com.github.hornta.commando.CarbonArgument;
import com.github.hornta.commando.CarbonCommand;
import com.github.hornta.commando.Commando;
import com.github.hornta.commando.ICarbonArgument;
import com.github.hornta.commando.completers.IArgumentHandler;
import se.hornta.messenger.MessageManager;
import se.hornta.messenger.MessagesBuilder;
import se.hornta.messenger.MessengerException;
import se.hornta.messenger.Translation;
import se.hornta.messenger.Translations;
import com.github.hornta.trollskogen_core.announcements.AnnouncementManager;
import com.github.hornta.trollskogen_core.bans.BanManager;
import com.github.hornta.trollskogen_core.commands.CommandHat;
import com.github.hornta.trollskogen_core.commands.CommandHelp;
import com.github.hornta.trollskogen_core.commands.CommandLinkAccept;
import com.github.hornta.trollskogen_core.commands.CommandLinkDecline;
import com.github.hornta.trollskogen_core.commands.CommandMaintenance;
import com.github.hornta.trollskogen_core.commands.CommandReload;
import com.github.hornta.trollskogen_core.commands.argumentHandlers.PlayerArgumentHandler;
import com.github.hornta.trollskogen_core.users.UserManager;
import com.github.hornta.trollskogen_core.users.UserObject;
import com.github.hornta.versioned_config.Configuration;
import com.github.hornta.versioned_config.ConfigurationBuilder;
import com.github.hornta.versioned_config.ConfigurationException;
import com.github.hornta.versioned_config.Migration;
import com.github.hornta.versioned_config.Patch;
import com.github.hornta.versioned_config.Type;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.luckperms.api.LuckPerms;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.asynchttpclient.Dsl.config;

public final class TrollskogenCorePlugin extends JavaPlugin {
	private static final Gson gson = new Gson();
	private static TrollskogenCorePlugin instance;
	private static AsyncHttpClient asyncHttpClient;
	private Configuration<ConfigKey> configuration;
	private AnnouncementManager announcementManager;
	private Commando commando;
	private UserManager userManager;
	private BanManager banManager;
	private Translations translations;
	private ICarbonArgument playerArg;
	private ServerReady serverReady;
	private LuckPerms luckPerms;
	private boolean isMaintenance;

	public static Plugin getPlugin() {
		return instance;
	}

	public static CompletableFuture<Void> request(String method, String path, JsonObject body, Consumer<Response> handler) {
		var builder = new RequestBuilder()
			.addHeader("API-key", (String) getConfiguration().get(ConfigKey.API_KEY))
			.addHeader("Content-Type", "application/json")
			.setMethod(method)
			.setUrl(getConfiguration().get(ConfigKey.API_URL) + path);

		if (body != null) {
			builder = builder.setBody(body.toString().getBytes(StandardCharsets.UTF_8));
		}

		if (body != null) {
			Bukkit.getLogger().info(method + " " + path + " " + body.toString());
		} else {
			Bukkit.getLogger().info(method + " " + path);
		}

		return asyncHttpClient.prepareRequest(builder.build())
			.execute()
			.toCompletableFuture()
			.thenApply((Response response) -> {
				gson.fromJson(response.getResponseBody(), Object.class);

				if (response.getStatusCode() != 200 && response.getStatusCode() != 201) {
					throw new IllegalArgumentException("Received bad http status");
				}
				return response;
			})
			.thenAccept(handler);
	}

	public static CompletableFuture<Void> request(String method, String path, Consumer<Response> handler) {
		return request(method, path, null, handler);
	}

	public static UserObject getUser(String name) {
		return instance.userManager.getUser(name);
	}

	public static UserObject getUser(UUID uuid) {
		return instance.userManager.getUser(uuid);
	}

	public static UserObject getUser(Player player) {
		return instance.userManager.getUser(player);
	}

	public static UserObject getUser(int id) {
		return instance.userManager.getUser(id);
	}

	public static Configuration<ConfigKey> getConfiguration() {
		return instance.configuration;
	}

	public static TrollskogenCorePlugin getInstance() {
		return instance;
	}

	public static ICarbonArgument getPlayerArg() {
		return instance.playerArg;
	}

	public static ServerReady getServerReady() {
		return instance.serverReady;
	}

	public static UserManager getUserManager() {
		return instance.userManager;
	}

	public static LuckPerms getLuckPerms() {
		return instance.luckPerms;
	}

	@Override
	public void onEnable() {
		instance = this;

		try {
			setupConfig();
		} catch (ConfigurationException e) {
			getLogger().log(Level.SEVERE, "Failed to setup config", e);
			setEnabled(false);
			return;
		}

		try {
			setupMessages();
		} catch (MessengerException e) {
			getLogger().log(Level.SEVERE, "Failed to setup messages", e);
			setEnabled(false);
			return;
		}
		setupCommands();

		var provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if (provider != null) {
			luckPerms = provider.getProvider();
		}

		var httpConfig = config();
		asyncHttpClient = asyncHttpClient(httpConfig);

		announcementManager = new AnnouncementManager(this);
		userManager = new UserManager(this);
		banManager = new BanManager(this);
		serverReady = new ServerReady();
		var discordListener = new DiscordListener();
		var linkManager = new LinkManager();

		getServer().getPluginManager().registerEvents(serverReady, this);
		getServer().getPluginManager().registerEvents(banManager, this);
		getServer().getPluginManager().registerEvents(userManager, this);
		getServer().getPluginManager().registerEvents(announcementManager, this);
		getServer().getPluginManager().registerEvents(new ExplodeListener(), this);
		getServer().getPluginManager().registerEvents(new WanderingTraderListener(), this);
		getServer().getPluginManager().registerEvents(discordListener, this);
		getServer().getPluginManager().registerEvents(linkManager, this);

		getServer().getScheduler().runTask(this, () -> userManager.loadAllUsers());

		try {
			JDABuilder
				.createDefault(configuration.get(ConfigKey.DISCORD_BOT_TOKEN))
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.addEventListeners(discordListener)
				.setActivity(Activity.listening("Skynet"))
				.enableIntents(GatewayIntent.GUILD_MEMBERS)
				.setChunkingFilter(ChunkingFilter.ALL)
				.build();
		} catch (Throwable e) {
			getLogger().log(Level.SEVERE, "Failed to connect to Discord", e);
		}
	}

	private void setupCommands() {
		commando = new Commando();

		IArgumentHandler playerArgumentHandler = new PlayerArgumentHandler(this);
		Bukkit.getPluginManager().registerEvents((Listener) playerArgumentHandler, this);
		playerArg = new CarbonArgument.Builder("player").setHandler(playerArgumentHandler).create();

		AnnouncementManager.setupCommands(this);
		BanManager.setupCommands(this, playerArg);


		commando.setNoPermissionHandler((CommandSender commandSender, CarbonCommand command) -> {
			MessageManager.sendMessage(commandSender, MessageKey.NO_PERMISSION);
		});

		commando.setMissingArgumentHandler((CommandSender commandSender, CarbonCommand command) -> {
			MessageManager.setValue("usage", command.getHelpText());
			MessageManager.sendMessage(commandSender, MessageKey.COMMAND_INCORRECT_NUM_ARGUMENTS);
		});

		commando.setMissingCommandHandler((CommandSender sender, List<CarbonCommand> suggestions) -> {
			MessageManager.setValue("suggestions", suggestions.stream()
				.map(CarbonCommand::getHelpText)
				.collect(Collectors.joining("\n")));
			MessageManager.sendMessage(sender, MessageKey.COMMAND_NOT_FOUND_SUGGESTIONS);
		});

		commando
			.addCommand("ts reload")
			.withHandler(new CommandReload(this))
			.requiresPermission("ts.reload");

		commando
			.addCommand("hat")
			.withHandler(new CommandHat(this))
			.requiresPermission("ts.hat")
			.preventConsoleCommandSender();

		commando
			.addCommand("ts help")
			.withHandler(new CommandHelp(this))
			.requiresPermission("ts.help");

		commando
			.addCommand("maintenance")
			.withHandler(new CommandMaintenance(this))
			.requiresPermission("ts.maintenance");

		commando
			.addCommand("ts link accept")
			.withHandler(new CommandLinkAccept())
			.preventConsoleCommandSender()
			.requiresPermission("ts.link.accept");

		commando
			.addCommand("ts link decline")
			.withHandler(new CommandLinkDecline())
			.preventConsoleCommandSender()
			.requiresPermission("ts.link.decline");
	}

	private void setupConfig() throws ConfigurationException {
		var cfgFile = new File(getDataFolder(), "config.yml");
		var cb = new ConfigurationBuilder<ConfigKey>(cfgFile);
		cb.addMigration(new Migration<>(1, () -> {
			var patch = new Patch<ConfigKey>();
			patch.set(ConfigKey.LANGUAGE, "language", "swedish", Type.STRING);
			patch.set(ConfigKey.MAINTENANCE, "maintenance", Arrays.asList("hornta", "philip2096"), Type.LIST);
			patch.set(ConfigKey.API_URL, "api_url", "http://localhost:3000", Type.STRING);
			patch.set(ConfigKey.API_KEY, "api_key", "", Type.STRING);
			patch.set(ConfigKey.ANNOUNCEMENT_INTERVAL, "announcement_interval", 1800, Type.INTEGER);
			return patch;
		}));
		cb.addMigration(new Migration<>(2, () -> {
			var patch = new Patch<ConfigKey>();
			patch.set(ConfigKey.DISCORD_BOT_TOKEN, "discord_bot_token", "REPLACE_ME", Type.STRING);
			patch.set(ConfigKey.DISCORD_GUILD_ID, "discord_guild_id", "REPLACE_ME", Type.STRING);
			return patch;
		}));
		configuration = cb.create();
	}

	private void setupMessages() throws MessengerException {
		var messageManager = new MessagesBuilder()
			.add(MessageKey.FIRST_JOIN_MESSAGE, "first-join-message")
			.add(MessageKey.CONFIG_RELOADED_SUCCESS, "config-reloaded-success")
			.add(MessageKey.HAT_SWITCH_SUCCESS, "hat-switch-success")
			.add(MessageKey.HAT_SWITCH_DENY, "hat-switch-deny")
			.add(MessageKey.PLAYER_NOT_FOUND, "player-not-found")
			.add(MessageKey.COMMAND_INCORRECT_NUM_ARGUMENTS, "command_incorrect_num_arguments")
			.add(MessageKey.COMMAND_NOT_FOUND_SUGGESTIONS, "command_not_found_suggestions")
			.add(MessageKey.NO_PERMISSION, "no_permission")
			.add(MessageKey.ANNOUNCEMENT_NOT_FOUND, "announcement_not_found")
			.add(MessageKey.ANNOUNCEMENT_SET, "announcement_set")
			.add(MessageKey.ANNOUNCEMENT_DELETED, "announcement_deleted")
			.add(MessageKey.ANNOUNCEMENT_LIST, "announcement_list")
			.add(MessageKey.HELP_TITLE, "help_title")
			.add(MessageKey.ANNOUNCEMENT, "announcement")
			.add(MessageKey.SECOND, "second")
			.add(MessageKey.SECONDS, "seconds")
			.add(MessageKey.PLAYER_BAN_TEMPORARY, "player_ban_temporary")
			.add(MessageKey.PLAYER_BAN_PERMANENT, "player_ban_permanent")
			.add(MessageKey.KICKBAN_PERMANENT, "kickban_permanent")
			.add(MessageKey.KICKBAN_TEMPORARY, "kickban_temporary")
			.add(MessageKey.ERR_BAN_NO_REASON, "err_ban_no_reason")
			.add(MessageKey.PLAYER_UNBAN, "player_unban")
			.add(MessageKey.UNBAN_NOT_BANNED, "unban_not_banned")
			.add(MessageKey.BAN_LIST_PLAYER_TEMPORARY, "ban_list_player_temporary")
			.add(MessageKey.BAN_LIST_PLAYER_PERMANENT, "ban_list_player_permanent")
			.add(MessageKey.NO_PERMISSION_PERMBAN, "no_permission_permban")
			.add(MessageKey.TOGGLE_MAINTENANCE_ON, "toggle_maintenance_on")
			.add(MessageKey.TOGGLE_MAINTENANCE_OFF, "toggle_maintenance_off")
			.add(MessageKey.TRY_JOIN_MAINTENANCE, "try_join_maintenance")
			.add(MessageKey.KICK_MAINTENANCE, "kick_maintenance")
			.build();

		translations = new Translations(this, messageManager);
		Translation translation = translations.createTranslation(configuration.get(ConfigKey.LANGUAGE));
		messageManager.setTranslation(translation);
	}

	public int scheduleSyncDelayedTask(Runnable run) {
		return getServer().getScheduler().scheduleSyncDelayedTask(this, run);
	}

	public AnnouncementManager getAnnouncementManager() {
		return announcementManager;
	}

	public Commando getCarbon() {
		return commando;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return commando.handleCommand(sender, command, args);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return commando.handleAutoComplete(sender, command, args);
	}

	public String getSeconds(int amount) {
		return MessageManager.getMessage(amount > 1 ? MessageKey.SECONDS : MessageKey.SECOND);
	}

	public boolean isMaintenance() {
		return isMaintenance;
	}

	public void toggleMaintenance() {
		isMaintenance = !isMaintenance;
	}

	public boolean isAllowedMaintenance(Player player) {
		List<String> allowedDuringMaintenance = configuration.get(ConfigKey.MAINTENANCE);
		for (String playerName : allowedDuringMaintenance) {
			if (player.getName().equalsIgnoreCase(playerName)) {
				return true;
			}
		}
		return false;
	}

	public BanManager getBanManager() {
		return banManager;
	}

	public Translations getTranslations() {
		return translations;
	}
}
