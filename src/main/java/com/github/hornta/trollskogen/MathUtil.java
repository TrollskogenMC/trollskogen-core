package com.github.hornta.trollskogen;

import java.util.Vector;

public class MathUtil {
  public static float randomRangeFloat(final float min, final float max) {
    return (float)((Math.random() < 0.5) ? ((1.0 - Math.random()) * (max - min) + min) : (Math.random() * (max - min) + min));
  }

  public static int randomRangeInt(final int min, final int max) {
    return (int)((Math.random() < 0.5) ? ((1.0 - Math.random()) * (max - min + 1) + min) : (Math.random() * (max - min + 1) + min));
  }
}
