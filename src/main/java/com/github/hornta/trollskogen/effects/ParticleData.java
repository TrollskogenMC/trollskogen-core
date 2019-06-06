package com.github.hornta.trollskogen.effects;

public abstract class ParticleData {
  private final EnumMaterial material;
  private final byte data;
  private final int[] packetData;

  ParticleData(EnumMaterial material, byte data) {
    this.material = material;
    this.data = data;
    this.packetData = new int[] { material.getTypeId(), data };
  }

  EnumMaterial getMaterial() {
    return this.material;
  }

  public byte getData() {
    return this.data;
  }

  public int[] getPacketData() {
    return this.packetData;
  }

  public String getPacketDataString() {
    return "_" + this.packetData[0] + "_" + this.packetData[1];
  }
}
