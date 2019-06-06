package com.github.hornta.trollskogen.racing.events;

import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.objects.RaceCheckpoint;

public class RaceCheckpointEvent extends RaceEvent {
  private RaceCheckpoint checkpoint;

  RaceCheckpointEvent(Race race, RaceCheckpoint checkpoint) {
    super(race);

    this.checkpoint = checkpoint;
  }

  public RaceCheckpoint getCheckpoint() {
    return checkpoint;
  }
}
