package com.github.hornta.trollskogen.commands;

import com.github.hornta.carbon.message.MessageManager;
import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.MessageKey;
import com.github.hornta.trollskogen.users.UserObject;
import com.github.hornta.trollskogen.users.events.UserVerifiedEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.asynchttpclient.Response;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.github.hornta.carbon.ICommandHandler;

import java.util.logging.Level;

public class CommandVerify implements ICommandHandler {
  private Main main;

  public CommandVerify(Main main) {
    this.main = main;
  }

  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    UserObject user = main.getUser((Player) sender);
    if(user.isVerified()) {
      MessageManager.sendMessage(sender, MessageKey.ALREADY_VERIFIED);
      return;
    }

    JsonObject body = new JsonObject();
    body.addProperty("id", user.getId());
    Main.request("PUT", "/create-token", body, (Response response) -> {
      Gson gson = new Gson();
      Token token;
      try {
        token = gson.fromJson(response.getResponseBody(), Token.class);
      } catch (JsonSyntaxException ex) {
        Bukkit.getScheduler().callSyncMethod(main, () -> {
          MessageManager.sendMessage(sender, MessageKey.VERIFY_ERROR);
          return null;
        });
        Bukkit.getLogger().log(Level.WARNING, ex.getMessage(), ex);
        return;
      }

      Bukkit.getScheduler().callSyncMethod(main, () -> {
        MessageManager.setValue("token", token.token);
        MessageManager.sendMessage(sender, MessageKey.VERIFIED_ON_DISCORD);
        return null;
      });
    })
    .exceptionally((Throwable t) -> {
      Bukkit.getLogger().log(Level.SEVERE, t.getMessage(), t);
      Bukkit.getScheduler().callSyncMethod(main, () -> {
        MessageManager.sendMessage(sender, MessageKey.VERIFY_ERROR);
        return null;
      });
      return null;
    });
  }

  private static class Token {
    private String token;
  }
}
