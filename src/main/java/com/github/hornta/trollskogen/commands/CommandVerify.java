package com.github.hornta.trollskogen.commands;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.User;
import com.github.hornta.trollskogen.events.UserVerifiedEvent;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.github.hornta.carbon.ICommandHandler;
import static org.asynchttpclient.Dsl.*;

import java.util.logging.Level;

public class CommandVerify implements ICommandHandler, Listener {
  private Main main;

  public CommandVerify(Main main) {
    this.main = main;
    main.getServer().getPluginManager().registerEvents(this, main);
  }

  @Override
  public void handle(CommandSender sender, String[] args, int typedArgs) {
    User user = main.getUser(sender);
    if(user.isDiscordVerified()) {
      main.getMessageManager().sendMessage(sender, "already_verified");
      return;
    }

    main.getAsyncHttpClient()
      .prepareGet(main.getTrollskogenConfig().getAPIUrl() + "/create-token")
      .addQueryParam("userId", user.getPlayer().getUniqueId().toString())
      .addQueryParam("lastSeenAs", user.getLastSeenAs())
      .setRequestTimeout(1000)
      .setReadTimeout(1000)
      .execute()
      .toCompletableFuture()
      .exceptionally((Throwable t) -> {
        Bukkit.getLogger().log(Level.SEVERE, t.getMessage(), t);
        Bukkit.getScheduler().callSyncMethod(main, () -> {
          main.getMessageManager().sendMessage(sender, "verify_error");
          return null;
        });
        return null;
      }).thenApply((Response response) -> {
        Gson gson = new Gson();
        Token token;
        try {
          token = gson.fromJson(response.getResponseBody(), Token.class);
        } catch (JsonSyntaxException ex) {
          Bukkit.getScheduler().callSyncMethod(main, () -> {
            main.getMessageManager().sendMessage(sender, "verify_error");
            return null;
          });
          Bukkit.getLogger().log(Level.WARNING, ex.getMessage(), ex);
          return null;
        }

        Bukkit.getScheduler().callSyncMethod(main, () -> {
          main.getMessageManager().setValue("token", token.token);
          main.getMessageManager().sendMessage(sender, "verified_on_discord");
          return null;
        });

        return response;
      });
  }

  private static class Token {
    private String token;
  }

  @EventHandler
  void onUserVerified(UserVerifiedEvent event) {
    event.getUser().setDiscordVerified(event.isVerified());
    if(event.getUser().getPlayer() instanceof Player) {
      if(event.isVerified()) {
        main.getMessageManager().setValue("player_name", event.getUser().getLastSeenAs());
        main.getMessageManager().broadcast("verified_success");
      }
    }
  }
}
