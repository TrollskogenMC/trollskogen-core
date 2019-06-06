package com.github.hornta.trollskogen.racing.events;

import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.objects.RaceStartPoint;

public class AddRaceStartPointEvent extends RaceStartPointEvent {
  public AddRaceStartPointEvent(Race race, RaceStartPoint startPoint) {
    super(race, startPoint);
  }
}

