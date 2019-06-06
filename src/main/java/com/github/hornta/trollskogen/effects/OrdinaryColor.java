package com.github.hornta.trollskogen.effects;

import org.bukkit.Color;

public final class OrdinaryColor extends ParticleColor
{
  private final int red;
  private final int green;
  private final int blue;
  private final float size;

  public OrdinaryColor(final int red, final int green, final int blue, final float size) throws IllegalArgumentException {
    if (red < 0) {
      throw new IllegalArgumentException("The red is lower than 0");
    }
    if (red > 255) {
      throw new IllegalArgumentException("The red is higher than 255");
    }
    this.red = red;
    if (green < 0) {
      throw new IllegalArgumentException("The green is lower than 0");
    }
    if (green > 255) {
      throw new IllegalArgumentException("The green is higher than 255");
    }
    this.green = green;
    if (blue < 0) {
      throw new IllegalArgumentException("The blue is lower than 0");
    }
    if (blue > 255) {
      throw new IllegalArgumentException("The blue is higher than 255");
    }
    this.blue = blue;
    if (size < 0.0f) {
      throw new IllegalArgumentException("The size is lower than 0");
    }
    if (size > 4.0f) {
      throw new IllegalArgumentException("The size is higher than 4");
    }
    this.size = size;
  }

  public OrdinaryColor(final Color color, final float size) {
    this(color.getRed(), color.getGreen(), color.getBlue(), size);
  }

  public int getRed() {
    return this.red;
  }

  public int getGreen() {
    return this.green;
  }

  public int getBlue() {
    return this.blue;
  }

  public float getSize() {
    return this.size;
  }

  @Override
  public float getValueX() {
    return this.red / 255.0f;
  }

  @Override
  public float getValueY() {
    return this.green / 255.0f;
  }

  @Override
  public float getValueZ() {
    return this.blue / 255.0f;
  }

  @Override
  public float getValueSize() {
    return this.size;
  }
}
