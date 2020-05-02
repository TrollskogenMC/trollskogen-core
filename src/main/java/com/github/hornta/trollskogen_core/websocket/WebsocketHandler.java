//package com.github.hornta.trollskogen_core.websocket;
//
//import com.github.hornta.trollskogen_core.ConfigKey;
//import com.github.hornta.trollskogen_core.TrollskogenCorePlugin;
//import com.github.hornta.trollskogen_core.users.UserManager;
//import com.github.hornta.trollskogen_core.users.UserObject;
//import com.github.hornta.trollskogen_core.users.events.UserVerifiedEvent;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonObject;
//import org.asynchttpclient.BoundRequestBuilder;
//import org.asynchttpclient.Response;
//import org.asynchttpclient.ws.WebSocket;
//import org.asynchttpclient.ws.WebSocketListener;
//import org.asynchttpclient.ws.WebSocketUpgradeHandler;
//import org.bukkit.Bukkit;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.player.AsyncPlayerChatEvent;
//
//import java.time.Instant;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.logging.Level;
//
//import static org.asynchttpclient.Dsl.asyncHttpClient;
//
//public class WebsocketHandler implements Listener {
//  private TrollskogenCorePlugin main;
//  private WebSocketUpgradeHandler wsHandler;
//  private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
//  private int attempt = 0;
//  private Timer reconnectTimer = new Timer();
//
//  public WebsocketHandler(TrollskogenCorePlugin main) {
//    this.main = main;
//    wsHandler = new WebSocketUpgradeHandler.Builder()
//      .addWebSocketListener(new WebSocketListener() {
//        private WebSocket webSocket;
//
//        @Override
//        public void onOpen(WebSocket webSocket) {
//          Bukkit.getLogger().info("connection opened");
//          this.webSocket = webSocket;
//          attempt = 0;
//        }
//
//        @Override
//        public void onClose(WebSocket webSocket, int i, String s) {
//          Bukkit.getLogger().info("connection closed");
//          this.webSocket = null;
//          reconnect();
//        }
//
//        @Override
//        public void onError(Throwable throwable) {
//          reconnect();
//        }
//
//        @Override
//        public void onTextFrame(String payload, boolean finalFragment, int rsv) {
//          Bukkit.getLogger().info(payload);
//
//          RuntimeTypeAdapterFactory<BaseClass> adapter = RuntimeTypeAdapterFactory
//            .of(BaseClass.class, "type")
//            .registerSubtype(Verified.class, "verified");
//
//          Gson gson = new GsonBuilder()
//            .registerTypeAdapterFactory(adapter)
//            .create();
//          BaseClass parsedMessage;
//          try {
//            parsedMessage = gson.fromJson(payload, BaseClass.class);
//          } catch (Throwable ex) {
//            Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
//            return;
//          }
//
//          if(parsedMessage instanceof Verified) {
//            Bukkit.getScheduler().callSyncMethod(main, () -> {
//              UserObject user = main.getUser(((Verified) parsedMessage).getUserId());
//              user.setVerified(((Verified) parsedMessage).isVerified());
//              user.setDiscordUserId(((Verified) parsedMessage).getDiscordUserId());
//              user.setVerifyDate(Instant.parse(((Verified) parsedMessage).getVerifyDate()));
//              user.setVerifyToken(((Verified) parsedMessage).getVerifyToken());
//              Bukkit.getPluginManager().callEvent(new UserVerifiedEvent(user));
//              return null;
//            });
//          }
//        }
//
//        @Override
//        public void onPingFrame(byte[] payload) {
//          webSocket.sendPongFrame();
//        }
//      })
//      .build();
//    openWebsocket();
//  }
//
//  @EventHandler
//  void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
//    if(event.isCancelled()) {
//      return;
//    }
//
//    Bukkit.getScheduler().callSyncMethod(main, () -> {
//      UserObject user = main.getUser(event.getPlayer());
//      if(user != null) {
//        scheduledExecutor.submit(() -> {
//          JsonObject json = new JsonObject();
//          json.addProperty("message", event.getMessage());
//          json.addProperty("user_id", user.getId());
//          json.addProperty("posted", UserManager.formatter.format(Instant.now()));
//          TrollskogenCorePlugin.request("POST", "/chat", json, (Response response) -> {
//            //
//          });
//        });
//      }
//      return null;
//    });
//  }
//
//  private void reconnect() {
//    TimerTask task = new TimerTask() {
//      public void run() {
//        attempt += 1;
//        Bukkit.getLogger().info("Reconnection attempt " + attempt);
//        openWebsocket();
//      }
//    };
//    Bukkit.getLogger().info("Scheduling a reconnect attempt.");
//    reconnectTimer.schedule(task, 5000L);
//  }
//
//  private void openWebsocket() {
//    BoundRequestBuilder requestBuilder = asyncHttpClient()
//      .prepareGet(main.getConfiguration().get(ConfigKey.WS_URL))
//      .addHeader("API-key", (String)main.getConfiguration().get(ConfigKey.API_KEY));
//
//      scheduledExecutor.submit(() -> {
//        try {
//          requestBuilder
//            .execute(wsHandler)
//            .get();
//        } catch (ExecutionException|InterruptedException e) {
//          Bukkit.getLogger().severe(e.getMessage());
//        }
//      });
//  }
//}
