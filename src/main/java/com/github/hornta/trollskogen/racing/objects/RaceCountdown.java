package com.github.hornta.trollskogen.racing.objects;

import com.github.hornta.trollskogen.Main;
import org.bukkit.scheduler.BukkitRunnable;

class RaceCountdown {
  private static final int HALF_SECOND = 10;
  private static final int ONE_SECOND = 20;
  public static final int COUNTDOWN_IN_SECONDS = 10;

  private Race race;
  private int countdown = COUNTDOWN_IN_SECONDS;
  private BukkitRunnable task;

  RaceCountdown(Race race) {
    this.race = race;
  }

  void start(Runnable callback) {
    this.countdown = COUNTDOWN_IN_SECONDS;
    task = new BukkitRunnable() {
      @Override
      public void run() {
        if(countdown == 0) {
          cancel();
          callback.run();
          return;
        }

        for(RacePlayerSession session : race.getPlayerSessions().values()) {
          // show the title for a tick longer to prevent blinking between titles
          session.getPlayer().sendTitle(String.valueOf(countdown), "sekunder innan racet börjar", 0, ONE_SECOND + 1, 0);
        }
        countdown -= 1;
      }
    };
    task.runTaskTimer(Main.getPlugin(), HALF_SECOND, ONE_SECOND);
  }

  void stop() {
    if(task != null) {
      task.cancel();
      task = null;
    }
  }
}
