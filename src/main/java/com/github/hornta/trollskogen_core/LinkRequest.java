package com.github.hornta.trollskogen_core;

import com.github.hornta.trollskogen_core.users.UserObject;
import net.dv8tion.jda.api.entities.User;

public class LinkRequest {
	private final UserObject user;
	private final User discordUser;

	LinkRequest(UserObject user, User discordUser) {
		this.user = user;
		this.discordUser = discordUser;
	}

	public UserObject getUser() {
		return user;
	}

	public User getDiscordUser() {
		return discordUser;
	}
}
