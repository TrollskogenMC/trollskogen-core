package com.github.hornta.trollskogen.racing.events;

import com.github.hornta.trollskogen.racing.objects.Race;

public class AddRaceEvent extends RaceEvent {
  public AddRaceEvent(Race race) {
    super(race);
  }
}
