package com.github.hornta.trollskogen.racing.events;

import com.github.hornta.trollskogen.racing.objects.Race;
import com.github.hornta.trollskogen.racing.objects.RacePlayerSession;

public class PlayerSessionFinishRaceEvent extends RaceEvent {
  private RacePlayerSession session;
  public PlayerSessionFinishRaceEvent(Race race, RacePlayerSession session) {
    super(race);
    this.session = session;
  }

  public RacePlayerSession getSession() {
    return session;
  }
}
