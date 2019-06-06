package com.github.hornta.trollskogen.racing.events;

import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.objects.RaceStartPoint;

public class RaceStartPointEvent extends RaceEvent {
  private RaceStartPoint startPoint;

  RaceStartPointEvent(Race race, RaceStartPoint startPoint) {
    super(race);

    this.startPoint = startPoint;
  }

  public RaceStartPoint getStartPoint() {
    return startPoint;
  }
}
