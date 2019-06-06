package com.github.hornta.trollskogen.racing.events;

import com.github.hornta.trollskogen.racing.objects.Race;

public class RaceStartEvent extends RaceEvent {
  public RaceStartEvent(Race race) {
    super(race);
  }
}
