package com.github.hornta.trollskogen_core.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.trollskogen_core.events.LinkDeclineEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLinkDecline implements ICommandHandler {
	@Override
	public void handle(CommandSender sender, String[] args, int typedArgs) {
		var event = new LinkDeclineEvent((Player) sender);
		Bukkit.getPluginManager().callEvent(event);
	}
}
