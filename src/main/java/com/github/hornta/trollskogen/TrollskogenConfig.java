package com.github.hornta.trollskogen;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import se.hornta.carbon.Config;

import java.util.Arrays;
import java.util.List;

public class TrollskogenConfig {
  private Config file;
  private ItemStack[] starterInventory;

  TrollskogenConfig(Main main) {
    this.file = new Config(main, "config.yml");
    this.file.saveDefault();
    this.load(this.file.getConfig());
  }

  private void load(FileConfiguration fc) {
    List<?> itemStacks = this.file.getConfig().getList("starter-inventory");
    if(itemStacks != null) {
      starterInventory = new ItemStack[itemStacks.size()];
      this.file.getConfig().getList("starter-inventory").toArray(starterInventory);
    }
  }

  private void save() {
    this.file.getConfig().set("starter-inventory", starterInventory);
    this.file.save();
  }

  public void setStarterKit(Inventory starterInventory) {
    this.starterInventory = new ItemStack[starterInventory.getContents().length];
    System.arraycopy(starterInventory.getContents(), 0, this.starterInventory, 0, starterInventory.getContents().length);
    this.save();
  }

  public ItemStack[] getStarterInventory() {
    return starterInventory;
  }
}
