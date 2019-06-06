package com.github.hornta.trollskogen.racing.events;

import com.github.hornta.trollskogen.racing.objects.Race;

public class EditingRaceEvent extends RaceEvent {
  private boolean isEditing;
  public EditingRaceEvent(Race race, boolean isEditing) {
    super(race);
    this.isEditing = isEditing;
  }

  public boolean isEditing() {
    return isEditing;
  }
}
