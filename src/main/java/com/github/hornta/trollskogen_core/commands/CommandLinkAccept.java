package com.github.hornta.trollskogen_core.commands;

import com.github.hornta.commando.ICommandHandler;
import com.github.hornta.trollskogen_core.events.LinkAcceptEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLinkAccept implements ICommandHandler {
	@Override
	public void handle(CommandSender sender, String[] args, int typedArgs) {
		var event = new LinkAcceptEvent((Player) sender);
		Bukkit.getPluginManager().callEvent(event);
	}
}
