package com.github.hornta.trollskogen.effects;

import com.github.hornta.trollskogen.MathUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum ParticleEffect {
  WATER_SPLASH("R", false, 5),
  FALLING_DUST("v", false, 46),
  DRIP_LAVA("k", false, 19),
  DRIP_WATER("l", false, 18),
  CRIT("h", true, 9),
  CRIT_MAGIC("p", true, 10),
  SPELL("n", false, 13),
  SPELL_INSTANT("B", false, 14),
  SPELL_MOB("s", false, 15),
  SPELL_WITCH("S", false, 17),
  SPELL_MOB_AMBIENT("a", false, 16),
  VILLAGER_ANGRY("b", false, 20),
  VILLAGER_HAPPY("z", false, 21),
  TOWN_AURA("H", false, 22),
  NOTE("I", false, 23),
  PORTAL("K", true, 24),
  ENCHANTMENT_TABLE("q", true, 25),
  FLAME("y", true, 26),
  REDSTONE("m", false, 30),
  HEART("A", false, 34),
  FIREWORKS_SPARK("w", true, 3),
  SMOKE_NORMAL("M", true, 11),
  SLIME("D", false, 33),
  DAMAGE_INDICATOR("i", true, 44);

//  BARRIER("c", false),
//  BLOCK_CRACK("d", false),
//  BLOCK_DUST("d", true),
//  BUBBLE_COLUMN_UP("f", false),
//  CLOUD("g", true, 29),
//  DRAGON_BREATH("j", true, 42),
//  DOLPHIN("X", false),
//  END_ROD("r", true, 43),
//  EXPLOSION_HUGE("t", false, 2),
//  EXPLOSION_LARGE("u", false, 1),
//  EXPLOSION_NORMAL("J", false, 0),
//  ITEM_CRACK("C", true, 36),
//  LAVA("G", false, 27),
//  MOB_APPEARANCE("o", false, 41),
//  NAUTILUS("W", false),
//  SMOKE_LARGE("F", true, 12),
//  SNOW_SHOVEL("J", true, 32),
//  SNOWBALL("E", false, 31),
//  SPIT("N", true, 48),
//  SUSPENDED("Q", false, 7),
//  SWEEP_ATTACK("O", true, 45),
//  TOTEM("P", true, 47),
//  WATER_BUBBLE("e", false, 4),
//  WATER_DROP("L", false, 39),
//  WATER_WAKE("x", false, 6),
//  SQUID_INK("V", false),
//  BUBBLE_POP("T", false),
//  CURRENT_DOWN("U", false);

  private String name;
  private boolean isVolatile;
  private int id;

  public static final int MAX_RANGE = 128;

  private static Map<String, ParticleEffect> BY_LABEL = new HashMap<>();

  static {
    for (ParticleEffect e: values()) {
      BY_LABEL.put(e.name(), e);
    }
  }

  ParticleEffect(String name, boolean isVolatile) {
    this.name = name;
    this.isVolatile = isVolatile;
    this.id = 0;
  }

  ParticleEffect(String name, boolean isVolatile, int id) {
    this.name = name;
    this.isVolatile = isVolatile;
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public boolean isVolatile() {
    return isVolatile;
  }

  public int getId() {
    return id;
  }

  public void display(final Location location) {
    new ParticlePacket(this, 0.0F, 0.0F, 0.0F, 0.0F, 1, true, null).sendTo(location, MAX_RANGE);
  }

  public void display(final Location location, final boolean hideForVanishedPlayer, final Player player) {
    new ParticlePacket(this, 0.0F, 0.0F, 0.0F, 0.0F, 1, true, null).sendTo(location, hideForVanishedPlayer, player);
  }

  public void display(final Location location, final Player player) {
    new ParticlePacket(this, 0.0F, 0.0F, 0.0F, 0.0F, 1, true, null).sendTo(location, player);
  }

  public void display(final Location location, final float speed) {
    new ParticlePacket(this, 0.0F, 0.0F, 0.0F, speed, 1, true, null).sendTo(location, MAX_RANGE);
  }

  public void display(final Location location, final Player player, final float speed) {
    new ParticlePacket(this, 0.0F, 0.0F, 0.0F, speed, 1, true, null).sendTo(location, player);
  }

  public void display(final Location location, final int amount) {
    new ParticlePacket(this, 0.0F, 0.0F, 0.0F, 0.0F, amount, true, null).sendTo(location, MAX_RANGE);
  }

  public void display(final Location location, final Player player, final int amount) {
    new ParticlePacket(this, 0.0F, 0.0F, 0.0F, 0.0F, amount, true, null).sendTo(location, player);
  }

  public void display(final Location location, final float speed, final int amount) {
    new ParticlePacket(this, 0.0F, 0.0F, 0.0F, speed, amount, true, null).sendTo(location, MAX_RANGE);
  }

  public void display(final Location location, final boolean hideForVanishedPlayer, final Player player, final float speed, final int amount) {
    new ParticlePacket(this, 0.0F, 0.0F, 0.0F, speed, amount, true, null).sendTo(location, hideForVanishedPlayer, player);
  }

  public void display(final Location location, final Player player, final float speed, final int amount) {
    new ParticlePacket(this, 0.0F, 0.0F, 0.0F, speed, amount, true, null).sendTo(location, player);
  }

  public void display(final Location location, final float offsetX, final float offsetY, final float offsetZ) {
    new ParticlePacket(this, offsetX, offsetY, offsetZ, 0.0F, 1, true, null).sendTo(location, MAX_RANGE);
  }

  public void display(final Location location, final Player player, final float offsetX, final float offsetY, final float offsetZ) {
    new ParticlePacket(this, offsetX, offsetY, offsetZ, 0.0F, 1, true, null).sendTo(location, player);
  }

  public void display(final Location location, final float offsetX, final float offsetY, final float offsetZ, final float speed) {
    new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, 1, true, null).sendTo(location, MAX_RANGE);
  }

  public void display(final Location location, final Player player, final float offsetX, final float offsetY, final float offsetZ, final float speed) {
    new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, 1, true, null).sendTo(location, player);
  }

  public void display(final Location location, final float offsetX, final float offsetY, final float offsetZ, final int amount) {
    new ParticlePacket(this, offsetX, offsetY, offsetZ, 0.0F, amount, true, null).sendTo(location, MAX_RANGE);
  }

  public void display(final Location location, final Player player, final float offsetX, final float offsetY, final float offsetZ, final int amount) {
    new ParticlePacket(this, offsetX, offsetY, offsetZ, 0.0F, amount, true, null).sendTo(location, player);
  }

  public void display(final Location location, final boolean hideForVanishedPlayer, final Player player, final float offsetX, final float offsetY, final float offsetZ, final int amount) {
    new ParticlePacket(this, offsetX, offsetY, offsetZ, 0.0F, amount, true, null).sendTo(location, hideForVanishedPlayer, player);
  }

  public void display(final Location location, final float offsetX, final float offsetY, final float offsetZ, final float speed, final int amount) {
    new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, true, null).sendTo(location, MAX_RANGE);
  }

  public void display(final Location location, final Player player, final float offsetX, final float offsetY, final float offsetZ, final float speed, final int amount) {
    new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, true, null).sendTo(location, player);
  }

  public void display(final Location location, final boolean hideForVanishedPlayer, final Player player, final float offsetX, final float offsetY, final float offsetZ, final float speed, final int amount) {
    new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, true, null).sendTo(location, hideForVanishedPlayer, player);
  }

  public void displayRandomColor(final Location location) {
    new ParticlePacket(this, new OrdinaryColor(MathUtil.randomRangeInt(1, 255), MathUtil.randomRangeInt(1, 255), MathUtil.randomRangeInt(1, 255), 1.0f), true).sendTo(location, MAX_RANGE);
  }

  public void displayColor(final Location location, final int red, final int green, final int blue) {
    new ParticlePacket(this, new OrdinaryColor(red, green, blue, 1.0f), true).sendTo(location, MAX_RANGE);
  }

  public void displayColor(final Location location, final int red, final int green, final int blue, int amount, int size) {
    new ParticlePacket(this, new OrdinaryColor(red, green, blue, 1.0f), true, amount, size).sendTo(location, MAX_RANGE);
  }

  public void displayColor(final Location location, final Player player, final int red, final int green, final int blue) {
    new ParticlePacket(this, new OrdinaryColor(red, green, blue, 1.0f), true).sendTo(location, player);
  }

  public void displayColor(final Location location, final Player player, final int red, final int green, final int blue, int amount, int size) {
    new ParticlePacket(this, new OrdinaryColor(red, green, blue, 1.0f), true, amount, size).sendTo(location, player);
  }

  public void displayColor(final Location location, final boolean hideForVanishedPlayer, final Player player, final int red, final int green, final int blue) {
    new ParticlePacket(this, new OrdinaryColor(red, green, blue, 1.0f), true).sendTo(location, hideForVanishedPlayer, player);
  }

  public void display(final ItemData data, final Location location, final float offsetX, final float offsetY, final float offsetZ, final float speed, final int amount) {
    new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, true, data).sendTo(location, MAX_RANGE);
  }

  public void display(final ItemData data, final Location location, final float offsetX, final float offsetY, final float offsetZ) {
    new ParticlePacket(this, offsetX, offsetY, offsetZ, 0.0F, 1, true, data).sendTo(location, MAX_RANGE);
  }

  public void display(final ItemData data, final Location location, final float speed, final int amount) {
    new ParticlePacket(this, 0.0F, 0.0F, 0.0F, speed, amount, true, data).sendTo(location, MAX_RANGE);
  }

  public void display(final ItemData data, final Location location, final float speed) {
    new ParticlePacket(this, 0.0F, 0.0F, 0.0F, speed, 1, true, data).sendTo(location, MAX_RANGE);
  }

  public void display(final ItemData data, final Location location, final int amount) {
    new ParticlePacket(this, 0.0F, 0.0F, 0.0F, 0.0F, amount, true, data).sendTo(location, MAX_RANGE);
  }

  public void display(final BlockData data, final Location location, final float offsetX, final float offsetY, final float offsetZ, final float speed, final int amount) {
    new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, true, data).sendTo(location, MAX_RANGE);
  }

  public void display(final BlockData data, final Location location, final float offsetX, final float offsetY, final float offsetZ) {
    new ParticlePacket(this, offsetX, offsetY, offsetZ, 0.0F, 1, true, data).sendTo(location, MAX_RANGE);
  }

  public void display(final BlockData data, final Location location, final float speed, final int amount) {
    new ParticlePacket(this, 0.0F, 0.0F, 0.0F, speed, amount, true, data).sendTo(location, MAX_RANGE);
  }

  public void display(final BlockData data, final Location location, final float speed) {
    new ParticlePacket(this, 0.0F, 0.0F, 0.0F, speed, 1, true, data).sendTo(location, MAX_RANGE);
  }

  public void display(final BlockData data, final Location location, final int amount) {
    new ParticlePacket(this, 0.0F, 0.0F, 0.0F, 0.0F, amount, true, data).sendTo(location, MAX_RANGE);
  }



  public static ParticleEffect getParticleEffect(String name) {
    if(name == null) {
      return null;
    }

    return ParticleEffect.BY_LABEL.get(name.toUpperCase(Locale.ENGLISH));
  }
}
