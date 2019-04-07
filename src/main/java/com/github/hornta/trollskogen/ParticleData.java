package com.github.hornta.trollskogen;

public abstract class ParticleData {
  private final byte data;

  ParticleData(final byte data) {
    this.data = data;
  }

  public byte getData() {
    return this.data;
  }
}
