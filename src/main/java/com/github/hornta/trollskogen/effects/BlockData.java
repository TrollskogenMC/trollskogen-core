package com.github.hornta.trollskogen.effects;

class BlockData extends ParticleData {
  BlockData(EnumMaterial material, byte data) {
    super(material, data);
    if (!material.getType().isBlock()) {
      throw new IllegalArgumentException("The material is not a block");
    }
  }
}
