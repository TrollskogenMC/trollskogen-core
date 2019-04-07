package com.github.hornta.trollskogen;

import org.apache.commons.lang.WordUtils;
import se.hornta.carbon.BaseTabCompleter;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EffectCompleter extends BaseTabCompleter<String> {
  private Main plugin;
  private List<String> items;

  public EffectCompleter(Main plugin) {
    this.plugin = plugin;
    items = Stream.of(ParticleEffect.values())
      .map(Enum::name)
      .map((s) -> WordUtils.capitalizeFully(s, new char[] { '_' }))
      .collect(Collectors.toList());
  }

  @Override
  public List<String> getItems(String argument) {
    return items.stream()
      .filter(id -> id.toLowerCase(Locale.ENGLISH).startsWith(argument.toLowerCase(Locale.ENGLISH)))
      .collect(Collectors.toList());
  }

  @Override
  public List<String> toSuggestions(List<String> items) {
    return items;
  }
}
