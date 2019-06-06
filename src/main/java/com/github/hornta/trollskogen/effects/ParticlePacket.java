package com.github.hornta.trollskogen.effects;

import com.github.hornta.trollskogen.MathUtil;
import com.github.hornta.trollskogen.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public final class ParticlePacket {
  private static int version;
  private static Class<?> enumParticle;
  private static Class<?> nmsPacketPlayOutParticles;
  private static Constructor<?> packetConstructor;
  private static Method getHandle;
  private static Field playerConnection;
  private static Method sendPacket;
  private static boolean initialized;
  private final ParticleEffect effect;
  private float offsetX;
  private final float offsetY;
  private final float offsetZ;
  private float size;
  private float speed;
  private int amount;
  private final boolean longDistance;
  private final ParticleData data;
  private Object packet;

  ParticlePacket(final ParticleEffect effect, final float offsetX, final float offsetY, final float offsetZ, final float speed, final int amount, final boolean longDistance, final ParticleData data) throws IllegalArgumentException {
    this.size = 1.0F;
    initialize();
    this.effect = effect;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.offsetZ = offsetZ;
    this.speed = speed;
    this.amount = amount;
    this.longDistance = longDistance;
    this.data = data;
    if (speed < 0.0f) {
      this.speed = 0.0f;
      throw new IllegalArgumentException("The speed is lower than 0");
    }
    if (amount < 0) {
      this.amount = 1;
      throw new IllegalArgumentException("The amount is lower than 0");
    }
  }

  public ParticlePacket(final ParticleEffect effect, final Vector direction, final float speed, final boolean longDistance, final ParticleData data) throws IllegalArgumentException {
    this(effect, (float)direction.getX(), (float)direction.getY(), (float)direction.getZ(), speed, 0, longDistance, data);
  }

  public ParticlePacket(final ParticleEffect effect, final ParticleColor color, final boolean longDistance) {
    this(effect, color.getValueX(), color.getValueY(), color.getValueZ(), 1.0f, 0, longDistance, null);
    this.size = color.getValueSize();
    if (effect == ParticleEffect.REDSTONE && color instanceof OrdinaryColor && ((OrdinaryColor)color).getRed() == 0) {
      this.offsetX = Float.MIN_NORMAL;
    }
  }

  public ParticlePacket(final ParticleEffect effect, final ParticleColor color, final boolean longDistance, int amount, int size) {
    this(effect, color.getValueX(), color.getValueY(), color.getValueZ(), 20.0f, 0, longDistance, null);
    this.size = color.getValueSize();
    if (effect == ParticleEffect.REDSTONE && color instanceof OrdinaryColor && ((OrdinaryColor)color).getRed() == 0) {
      this.offsetX = Float.MIN_NORMAL;
    }
    this.size = size;
    this.amount = amount;
  }

  public static void initialize() {
    if (ParticlePacket.initialized) {
      return;
    }
    try {
      ParticlePacket.version = Integer.parseInt(ReflectionUtils.PackageType.getServerVersion().split("\\_")[1]);
      if (ParticlePacket.version > 7) {
        ParticlePacket.enumParticle = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("Particles");
      }
      ParticlePacket.nmsPacketPlayOutParticles = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("PacketPlayOutWorldParticles");
      ParticlePacket.packetConstructor = ReflectionUtils.getConstructor(ParticlePacket.nmsPacketPlayOutParticles, (Class<?>[])new Class[0]);
      ParticlePacket.getHandle = ReflectionUtils.getMethod("CraftPlayer", ReflectionUtils.PackageType.CRAFTBUKKIT_ENTITY, "getHandle", (Class<?>[])new Class[0]);
      ParticlePacket.playerConnection = ReflectionUtils.getField("EntityPlayer", ReflectionUtils.PackageType.MINECRAFT_SERVER, false, "playerConnection");
      ParticlePacket.sendPacket = ReflectionUtils.getMethod(ParticlePacket.playerConnection.getType(), "sendPacket", ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("Packet"));
    }
    catch (Exception exception) {
      throw new VersionIncompatibleException("Your current bukkit version seems to be incompatible with this library", exception);
    }
    ParticlePacket.initialized = true;
  }

  public static int getVersion() {
    if (!ParticlePacket.initialized) {
      initialize();
    }
    return ParticlePacket.version;
  }

  public static boolean isInitialized() {
    return ParticlePacket.initialized;
  }

  private void initializePacket(final Location center) {
    if (this.packet != null) {
      return;
    }
// /*this.effect == ParticleEffect.BLOCK_CRACK || this.effect == ParticleEffect.BLOCK_DUST || */
    try {
      Object particle = ParticlePacket.enumParticle.getField(this.effect.getName()).get(this.effect.getName());
      if (this.effect == ParticleEffect.REDSTONE) {

        if (this.offsetX != 0.0F || this.offsetY != 0.0F || this.offsetZ != 0.0F) {
          particle = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("ParticleParamRedstone")
            .getConstructor(Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE)
            .newInstance(this.offsetX, this.offsetY, this.offsetZ, this.size);
        } else {
          particle = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("ParticleParamRedstone")
            .getConstructor(Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE)
            .newInstance(
              MathUtil.randomRangeFloat(0.0F, 1.0F),
              MathUtil.randomRangeFloat(0.0F, 1.0F),
              MathUtil.randomRangeFloat(0.0F, 1.0F),
              this.size);
        }
      } else if (this.effect == ParticleEffect.FALLING_DUST) {
//        ParticleData newData = this.data;
//        if (newData == null) {
//          newData = new BlockData(EnumMaterial.GRASS_BLOCK, (byte)0);
//        }
//        final Class<?> blockStateClass = ReflectionUtils.PackageType.CRAFTBUKKIT.getClass("block.CraftBlockState");
//        final Object material = blockStateClass.getConstructor(Material.class).newInstance(newData.getMaterial().getType());
//        blockStateClass.getDeclaredMethod("setRawData", Byte.TYPE).invoke(material, 10);
//        final Object blockHandle = blockStateClass.getDeclaredMethod("getHandle", (Class<?>[])new Class[0]).invoke(material, new Object[0]);
//        particle = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("ParticleParamBlock").getConstructor(ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("Particle"), ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("IBlockData")).newInstance(particle, blockHandle);

        ///////////////////

        ParticleData newData = this.data;
        if (newData == null) {
          newData = new BlockData(EnumMaterial.GRASS_BLOCK, (byte)0);
        }
        Class<?> blockStateClass = ReflectionUtils.PackageType.CRAFTBUKKIT.getClass("block.CraftBlockState");
        Object material = blockStateClass
          .getConstructor(Material.class)
          .newInstance(newData.getMaterial().getType());

        blockStateClass
          .getDeclaredMethod("setRawData", Byte.TYPE)
          .invoke(material, Byte.valueOf("10"));

        Object blockHandle = blockStateClass
          .getDeclaredMethod("getHandle")
          .invoke(material);

        particle = ReflectionUtils.PackageType.MINECRAFT_SERVER
          .getClass("ParticleParamBlock")
          .getConstructor(
            ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("Particle"),
            ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("IBlockData"))
          .newInstance(particle, blockHandle);

      } else {
        //
      }
//      else if (this.effect == ParticleEffect.ITEM_CRACK) {
//        final Object itemStack = ReflectionUtils.PackageType.CRAFTBUKKIT.getClass("inventory.CraftItemStack").getDeclaredMethod("asCraftCopy", ItemStack.class).invoke(null, new ItemStack((this.data == null) ? EnumMaterial.GRASS_BLOCK.getType() : this.data.getMaterial().getType()));
//        final Object nmsItemStack = ReflectionUtils.PackageType.CRAFTBUKKIT.getClass("inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(null, itemStack);
//        particle = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("ParticleParamItem").getConstructor(ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("Particle"), ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("ItemStack")).newInstance(particle, nmsItemStack);
//      }
      this.packet = ParticlePacket.nmsPacketPlayOutParticles.getConstructor(
          ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("ParticleParam"),
          Boolean.TYPE,
          Float.TYPE,
          Float.TYPE,
          Float.TYPE,
          Float.TYPE,
          Float.TYPE,
          Float.TYPE,
          Float.TYPE,
          Integer.TYPE)
        .newInstance(
          particle,
          true,
          (float)center.getX(),
          (float)center.getY(),
          (float)center.getZ(),
          this.offsetX,
          this.offsetY,
          this.offsetZ,
          this.speed,
          this.amount);
    }
    catch (Exception exception) {
      throw new PacketInstantiationException("Packet instantiation failed", exception);
    }
  }

  public void sendTo(final Location center, final Player player) throws PacketInstantiationException, PacketSendingException {
    this.initializePacket(center);
    try {
      ParticlePacket.sendPacket.invoke(ParticlePacket.playerConnection.get(ParticlePacket.getHandle.invoke(player, new Object[0])), this.packet);
    }
    catch (Exception exception) {
      throw new PacketSendingException("Failed to send the packet to player '" + player.getName() + "'", exception);
    }
  }

  public void sendTo(final Location center, final boolean hideForVanishedPlayer, final Player targetPlayer) throws IllegalArgumentException {
    if (targetPlayer == null) {
      throw new IllegalArgumentException("The player is null.");
    }
    if (hideForVanishedPlayer) {
      for (final Player viewer : Bukkit.getOnlinePlayers()) {
        if ((viewer.canSee(targetPlayer) && targetPlayer.getGameMode() != GameMode.SPECTATOR) || viewer == targetPlayer) {
          this.sendTo(center, viewer);
        }
      }
    }
    else {
      this.sendTo(center, ParticleEffect.MAX_RANGE);
    }
  }

  public void sendTo(final Location center, final List<Player> players) throws IllegalArgumentException {
    if (players.isEmpty()) {
      throw new IllegalArgumentException("The player list is empty");
    }
    for (final Player player : players) {
      this.sendTo(center, player);
    }
  }

  public void sendTo(final Location center, final double range) throws IllegalArgumentException {
    if (range < 1.0) {
      throw new IllegalArgumentException("The range is lower than 1");
    }
    final String worldName = center.getWorld().getName();
    final double squared = range * range;
    for (final Player player : Bukkit.getOnlinePlayers()) {
      if (player.getWorld().getName().equals(worldName)) {
        if (player.getLocation().distanceSquared(center) > squared) {
          continue;
        }
        this.sendTo(center, player);
      }
    }
  }

  private static final class VersionIncompatibleException extends RuntimeException
  {
    private static final long serialVersionUID = 3203085387160737484L;

    public VersionIncompatibleException(final String message, final Throwable cause) {
      super(message, cause);
    }
  }

  private static final class PacketInstantiationException extends RuntimeException
  {
    private static final long serialVersionUID = 3203085387160737484L;

    public PacketInstantiationException(final String message, final Throwable cause) {
      super(message, cause);
    }
  }

  private static final class PacketSendingException extends RuntimeException
  {
    private static final long serialVersionUID = 3203085387160737484L;

    public PacketSendingException(final String message, final Throwable cause) {
      super(message, cause);
    }
  }
}
