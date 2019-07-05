package com.github.hornta.trollskogen.racing.mcmmo;

import com.github.hornta.trollskogen.Main;
import com.github.hornta.trollskogen.racing.enums.RaceState;
import com.github.hornta.trollskogen.racing.objects.Race;
import com.gmail.nossr50.events.hardcore.McMMOPlayerDeathPenaltyEvent;
import com.gmail.nossr50.events.hardcore.McMMOPlayerVampirismEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class McMMOListener implements Listener {
  @EventHandler
  void onMcMMOPlayerDeathPenalty(McMMOPlayerDeathPenaltyEvent event) {
    Race race = Main.getRacing().getParticipatingRace(event.getPlayer());
    if(race != null && race.getState() == RaceState.COUNTDOWN || race.getState() == RaceState.STARTED) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  void onMcMMOPlayerVampirism(McMMOPlayerVampirismEvent event) {
    if(Main.getRacing().getParticipatingRace(event.getPlayer()) != null) {
      event.setCancelled(true);
    }
  }
}
