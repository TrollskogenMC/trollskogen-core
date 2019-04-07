package com.github.hornta.trollskogen;

public class MathUtil {
  public static float randomRangeFloat(final float min, final float max) {
    return (float)((Math.random() < 0.5) ? ((1.0 - Math.random()) * (max - min) + min) : (Math.random() * (max - min) + min));
  }
}
