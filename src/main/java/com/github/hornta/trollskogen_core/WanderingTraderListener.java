package com.github.hornta.trollskogen_core;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

public class WanderingTraderListener implements Listener {
  private static final Random random = new Random();
  private final LinkedHashMap<Integer, Integer> maxUsesWeights;
  private final LinkedHashMap<Number, Integer> durabilityWeights;
  private final LinkedHashMap<Integer, Integer> unbreakingWeights;

  WanderingTraderListener() {
    maxUsesWeights = new LinkedHashMap<>();
    maxUsesWeights.put(1, 60);
    maxUsesWeights.put(2, 25);
    maxUsesWeights.put(3, 15);

    durabilityWeights = new LinkedHashMap<>();
    durabilityWeights.put(1, 10);
    durabilityWeights.put(0.75, 15);
    durabilityWeights.put(0.4, 25);
    durabilityWeights.put(0.2, 50);

    unbreakingWeights = new LinkedHashMap<>();
    unbreakingWeights.put(1, 50);
    unbreakingWeights.put(2, 35);
    unbreakingWeights.put(3, 15);
  }

  @EventHandler
  public void onSpawn(CreatureSpawnEvent entity) {
    if (entity.getEntity() instanceof WanderingTrader) {
      if(random.nextBoolean()) {
        return;
      }
      var wanderingTrader = (WanderingTrader) entity.getEntity();
      var recipes = wanderingTrader.getRecipes();
      var hasElytraTrade = false;

      for(var recipe : recipes) {
        for(var ingredient : recipe.getIngredients()) {
          if(ingredient.getType() == Material.ELYTRA) {
            hasElytraTrade = true;
            break;
          }
        }

        if(hasElytraTrade) {
          break;
        }
      }

      if (!hasElytraTrade) {
        var maxUses = randomByWeights(maxUsesWeights);

        var newTrades = new ArrayList<>(wanderingTrader.getRecipes());
        var durability = randomByWeights(durabilityWeights);
        var elytra = new ItemStack(Material.ELYTRA, 1);
        var dmg = (Damageable) elytra.getItemMeta();
        var ELYTRA_MAX_DURABILITY = 432;
        dmg.setDamage(Math.round(ELYTRA_MAX_DURABILITY * durability.floatValue()));
        elytra.setItemMeta((ItemMeta) dmg);

        var itemMeta = elytra.getItemMeta();

        if(randomIntBetween(1, 10) == 10) {
          itemMeta.addEnchant(Enchantment.MENDING, 1, true);
        }

        if(randomIntBetween(1, 10) >= 8) {
          var unbreaking = randomByWeights(unbreakingWeights);
          itemMeta.addEnchant(Enchantment.DURABILITY, unbreaking, true);
        }
        elytra.setItemMeta(itemMeta);

        var elytraTrade = new MerchantRecipe(elytra, 0, maxUses, false);
        elytraTrade.addIngredient(new ItemStack(Material.EMERALD_BLOCK, randomIntBetween(16,45)));
        elytraTrade.addIngredient(new ItemStack(Material.DIAMOND, randomIntBetween(48,64)));
        newTrades.add(elytraTrade);

        wanderingTrader.setRecipes(newTrades);
      }
    }
  }

  public static <T> T randomByWeights(LinkedHashMap<T, Integer> weightMap) {
    var sum = 0;
    for(var weight : weightMap.values()) {
      sum += weight;
    }

    var target = randomIntBetween(1, sum);
    T value = null;
    for(var entry : weightMap.entrySet()) {
      var weight = entry.getValue();
      if(target <= weight) {
        value =  entry.getKey();
        break;
      } else {
        target -= weight;
      }
    }

    return value;
  }

  private static int randomIntBetween(int min, int max) {
    return random.nextInt((max - min) + 1) + min;
  }
}
