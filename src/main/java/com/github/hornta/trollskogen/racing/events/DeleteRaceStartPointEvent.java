package com.github.hornta.trollskogen.racing.events;

import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.objects.RaceStartPoint;

public class DeleteRaceStartPointEvent extends RaceStartPointEvent {
  public DeleteRaceStartPointEvent(Race race, RaceStartPoint startPoint) {
    super(race, startPoint);
  }
}

