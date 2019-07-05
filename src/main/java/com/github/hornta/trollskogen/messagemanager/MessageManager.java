package com.github.hornta.trollskogen.messagemanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {
  private static final Pattern placeholderPattern = Pattern.compile("<[a-z_]+>", Pattern.CASE_INSENSITIVE);
  private Map<String, String> placeholderValues = new HashMap<>();
  private Config config;

  public MessageManager(JavaPlugin plugin) {
    config = new Config(plugin, "messages.yml");
    config.saveDefault();
  }

  public Config getConfig() {
    return config;
  }

  public void setValue(String key, Object value) {
    if (value == null) {
      value = "";
    }

    placeholderValues.put(key.toLowerCase(Locale.ENGLISH), value.toString());
  }

  public String getValue(String key) {
    if (placeholderValues.containsKey(key)) {
      return placeholderValues.get(key);
    }

    return null;
  }

  public void broadcast(String key) {
    String message = config.getConfig().getString(key);

    if (message == null) {
      Bukkit.getLogger().severe("Message " + key + " wasn't found.");
      return;
    }

    message = transformPlaceholders(message, placeholderValues);
    message = transformColors(message);

    Bukkit.broadcastMessage(message);
  }

  public void sendMessage(String key, List<CommandSender> entities, Map<String, String> values) {
    String message = config.getConfig().getString(key.toLowerCase(Locale.ENGLISH));

    if (message == null) {
      Bukkit.getLogger().severe("Message " + key + " wasn't found.");
      for (CommandSender sender : entities) {
        if (sender == null) {
          continue;
        }
        sender.sendMessage(key);
      }

      return;
    }

    if(values == null) {
      message = transformPlaceholders(message, placeholderValues);
    } else {
      message = transformPlaceholders(message, values);
    }
    message = transformColors(message);

    for (CommandSender sender : entities) {
      if (sender == null) {
        continue;
      }
      sender.sendMessage(message);
    }
  }

  public void sendMessage(CommandSender sender, String key) {
    sendMessage(key, Collections.singletonList(sender), null);
  }

  public void sendMessage(CommandSender sender, String key, Map<String, String> values) {
    sendMessage(key, Collections.singletonList(sender), values);
  }

  public String getMessage(String key) {
    String message = config.getConfig().getString(key.toLowerCase(Locale.ENGLISH));

    if (message == null) {
      Bukkit.getLogger().log(Level.SEVERE, "Message " + key + " wasn't found.");
      return key;
    }

    message = transformPlaceholders(message, placeholderValues);
    message = transformColors(message);

    return message;
  }

  public String transformColors(String input) {
    return ChatColor.translateAlternateColorCodes('&', input);
  }

  private String transformPlaceholders(String input, Map<String, String> values) {
    String transformed = transformPattern(input, placeholderPattern, values);
    values.clear();
    return transformed;
  }

  static String transformPattern(String input, Pattern pattern, Map<String, String> values) {
    return StringReplacer.replace(input, pattern, (Matcher m) -> {
      String placeholder = m.group().substring(1, m.group().length() - 1).toLowerCase(Locale.ENGLISH);
      if (values.containsKey(placeholder)) {
        return values.get(placeholder);
      } else {
        return m.group();
      }
    });
  }
}
