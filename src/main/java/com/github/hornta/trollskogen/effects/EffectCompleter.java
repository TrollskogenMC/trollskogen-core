package com.github.hornta.trollskogen.effects;

import com.github.hornta.BaseTabCompleter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EffectCompleter implements BaseTabCompleter {
  private List<String> items;

  public EffectCompleter() {
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
}
