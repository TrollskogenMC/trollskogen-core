package com.github.hornta.trollskogen_core.config;

import com.github.hornta.trollskogen_core.ConfigKey;
import com.github.hornta.versioned_config.Configuration;
import com.github.hornta.versioned_config.IConfigVersion;
import com.github.hornta.versioned_config.Patch;
import com.github.hornta.versioned_config.Type;

import java.util.Arrays;

public class InitialVersion implements IConfigVersion<ConfigKey> {
  @Override
  public int version() {
    return 1;
  }

  @Override
  public Patch<ConfigKey> migrate(Configuration<ConfigKey> configuration) {
    Patch<ConfigKey> patch = new Patch<>();
    patch.set(ConfigKey.LANGUAGE, "language", "swedish", Type.STRING);
    patch.set(ConfigKey.MAINTENANCE, "maintenance", Arrays.asList("hornta", "philip2096"), Type.LIST);
    patch.set(ConfigKey.API_URL, "api_url", "http://localhost:3000", Type.STRING);
    patch.set(ConfigKey.API_KEY, "api_key", "", Type.STRING);
    patch.set(ConfigKey.ANNOUNCEMENT_INTERVAL, "announcement_interval", 1800, Type.INTEGER);
    return patch;
  }
}
