package com.github.hornta.trollskogen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.asynchttpclient.Response;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.function.Consumer;
import java.util.logging.Level;

public class HandleRequest implements Consumer<Response> {
  private Consumer<JsonElement> consumer;

  public HandleRequest(Consumer<JsonElement> consumer) {
    this.consumer = consumer;
  }

  @Override
  public void accept(Response response) {
    try {
      JsonParser parser = new JsonParser();
      JsonElement jsonElement = parser.parse(response.getResponseBody());

      if(jsonElement.isJsonObject()) {
        JsonElement error = jsonElement.getAsJsonObject().get("error");
        if(error != null && error.isJsonObject()) {
          JsonObject errorObject = error.getAsJsonObject();
          JsonElement errorMessage = errorObject.get("message");
          if(errorMessage.isJsonPrimitive()) {
            String message = errorMessage.getAsString();
            Bukkit.getLogger().log(Level.SEVERE, message);
            return;
          }
        }
      }

      consumer.accept(jsonElement);
    } catch(Throwable e) {
      Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
    }
  }
}
