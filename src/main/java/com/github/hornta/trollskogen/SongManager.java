package com.github.hornta.trollskogen;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongManager {
  private Map<String, Song> songsByName;
  private List<String> songNames;
  private Map<Player, RadioSongPlayer> playerRadios = new HashMap<>();

  SongManager(Main main) {
    File songsFolder = new File(main.getDataFolder(), "songs");
    if(!songsFolder.exists()) {
      songsFolder.mkdir();
    }

    songsByName = new HashMap<>();
    songNames = new ArrayList<>();

    File[] files = songsFolder.listFiles();
    for(File file : files) {
      if(file.isFile()) {
        String name = getNameWithoutExtension(file.getName()).replaceAll(" ", "_").replaceAll("[^A-Za-z0-9_]", "");
        Song song = NBSDecoder.parse(file);
        songsByName.put(name, song);
        songNames.add(name);
      }
    }
    Bukkit.getLogger().info("Loaded " + songNames.size() + " songs.");
  }

  private static String getNameWithoutExtension(String name) {
    int pos = name.lastIndexOf(".");
    if (pos > 0) {
      name = name.substring(0, pos);
    }
    return name;
  }

  public List<String> getSongNames() {
    return songNames;
  }

  public Song getSongByName(String name) {
    return songsByName.get(name);
  }

  public void playSong(Song song, Player player) {
    stopSong(player);
    RadioSongPlayer radio = new RadioSongPlayer(song);
    radio.addPlayer(player);
    radio.setPlaying(true);
    playerRadios.put(player, radio);
  }

  public void stopSong(Player player) {
    if(playerRadios.containsKey(player)) {
      RadioSongPlayer radio = playerRadios.get(player);
      radio.setPlaying(false);
      radio.destroy();
      playerRadios.remove(player);
    }
  }
}
