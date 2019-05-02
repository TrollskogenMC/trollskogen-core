package com.github.hornta.trollskogen.completers;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.ParticleEffect;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;
import se.hornta.carbon.BaseTabCompleter;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EffectCompleter extends BaseTabCompleter {
  private Main plugin;
  private List<String> items;

  public EffectCompleter(Main plugin) {
    this.plugin = plugin;
    items = Stream.of(ParticleEffect.values())
      .map(Enum::name)
      .map(s -> WordUtils.capitalizeFully(s, new char[] { '_' }))
      .collect(Collectors.toList());
  }

  @Override
  public List<String> getItems(CommandSender sender, String[] arguments) {
    return items.stream()
      .filter(id -> id.toLowerCase(Locale.ENGLISH).startsWith(arguments[0].toLowerCase(Locale.ENGLISH)))
      .collect(Collectors.toList());
  }

  @Override
  public List<String> toSuggestions(CommandSender sender, List<String> items) {
    return items;
  }
}
