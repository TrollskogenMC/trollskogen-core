package com.github.hornta.trollskogen.racing.events;

import com.github.hornta.trollskogen.racing.objects.Race;

public class ChangeRaceNameEvent extends RaceEvent {
  private String oldName;

  public ChangeRaceNameEvent(Race race, String oldName) {
    super(race);
    this.oldName = oldName;
  }

  public String getOldName() {
    return oldName;
  }
}
