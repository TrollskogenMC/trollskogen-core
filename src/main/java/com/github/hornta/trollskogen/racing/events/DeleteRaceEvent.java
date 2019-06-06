package com.github.hornta.trollskogen.racing.events;

import com.github.hornta.trollskogen.racing.objects.Race;

public class DeleteRaceEvent extends RaceEvent {
  public DeleteRaceEvent(Race race) {
    super(race);
  }
}
