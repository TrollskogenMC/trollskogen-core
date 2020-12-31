package com.github.hornta.trollskogen_core.users;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.UUID;

public class UserObject {
	private final Integer id;
	private String name;
	private UUID uuid;
	private String discordUserId;
	private Instant lastJoinDate;

	public UserObject(int id, String name, UUID uuid, String discordUserId, Instant lastJoinDate) {
		this.id = id;
		this.name = name;
		this.uuid = uuid;
		this.discordUserId = discordUserId;
		this.lastJoinDate = lastJoinDate;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getDiscordUserId() {
		return discordUserId;
	}

	public void setDiscordUserId(String discordUserId) {
		this.discordUserId = discordUserId;
	}

	public Instant getLastJoinDate() {
		return lastJoinDate;
	}

	public void setLastJoinDate(Instant lastJoinDate) {
		this.lastJoinDate = lastJoinDate;
	}

	public String getFormattedLastJoinDate() {
		if (lastJoinDate == null) {
			return null;
		}

		return UserManager.formatter.format(lastJoinDate);
	}

	public boolean isOnline() {
		return Bukkit.getPlayer(uuid) != null;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}
}
