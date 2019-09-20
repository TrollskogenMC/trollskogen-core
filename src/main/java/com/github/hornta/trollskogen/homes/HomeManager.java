package com.github.hornta.trollskogen.homes;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.hornta.trollskogen.User;
import com.github.hornta.trollskogen.events.DeleteHomeEvent;
import com.github.hornta.trollskogen.events.ReadUsersEvent;
import com.github.hornta.trollskogen.events.SetHomeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HomeManager implements Listener {
  private RTree<Home, Point> tree;

  public HomeManager()  {
    tree = RTree.create();
  }

  public Home getNearestPublicHome(Player player) {
    Entry<Home, Point> homePointEntry = tree
      .search(Geometries.circle(player.getLocation().getX(), player.getLocation().getZ(), 32))
      .filter((Entry<Home, Point> entry) -> {
        if(!entry.value().isPublic()) {
          return false;
        }

        return !entry.value().getOwner().equals(player.getUniqueId());
      }).toBlocking().firstOrDefault(null);

    if(homePointEntry == null) {
      return null;
    }

    return homePointEntry.value();
  }

  @EventHandler
  void onReadUsers(ReadUsersEvent event) {
    for(User user : event.getUsers().values()) {
      for(Home home : user.getHomes()) {
        tree = tree.add(home, Geometries.point(home.getLocation().getX(), home.getLocation().getZ()));
      }
    }
  }

  @EventHandler
  void onSetHome(SetHomeEvent event) {
    if(event.getPreviousGeometry() != null) {
      tree = tree.delete(event.getHome(), event.getPreviousGeometry());
    }
    tree = tree.add(event.getHome(), event.getHome().getGeometry());
  }

  @EventHandler
  void onDeleteHome(DeleteHomeEvent event) {
    tree = tree.delete(event.getHome(), event.getHome().getGeometry());
  }
}
