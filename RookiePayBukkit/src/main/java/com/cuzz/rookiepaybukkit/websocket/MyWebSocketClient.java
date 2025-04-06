package com.cuzz.rookiepaybukkit.websocket;


import com.cuzz.rookiepaybukkit.RookiePayBukkit;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;


import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.*;

public class MyWebSocketClient extends WebSocketClient {

    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }


    @Override
    public void onMessage(String message) {
        // 使用新线程处理消息
        new Thread(() -> {
            Gson gson = new Gson();
            // 解析 JSON 字符串
            JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
            processMessage(jsonObject);

        }).start();

        System.out.println("📩 收到消息: " + message);
    }

    public void processMessage(JsonObject jsonObject){
        //若为响应则 完成对应的completeFuture
        if (jsonObject.has("echo")) {
            UUID uuid = UUID.fromString(jsonObject.get("echo").getAsString());
            CompletableFuture<JsonObject> response = responseMap.get(uuid);
            if (response != null) {
                response.complete(jsonObject);
                return;
            }
        }
        //否则打印消息
        Gson gson = new Gson();
        String json = gson.toJson(jsonObject);
        System.out.println();
        Bukkit.getOnlinePlayers().forEach(
                player -> {
                    Bukkit.getScheduler().runTask(RookiePayBukkit.INSTANCE,
                        ()->{
                            player.sendMessage("完成支付"+json);
                            player.closeInventory();
                        }
                    );
                }

        );

    };


    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("❌ WebSocket 连接关闭，原因：" + reason);

        // 3 秒后尝试重连
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("🔄 尝试重新连接...");
                this.reconnect();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    @Override
    public void onError(Exception ex) {
        System.out.println("⚠️ WebSocket 发生错误: " + ex.getMessage());
    }
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("🔗 WebSocket 连接已建立");

        // 启动心跳定时器，定期发送心跳消息
        Timer heartbeatTimer = new Timer(true);
        heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isOpen()) {
                    sendHeartbeat();  // 发送心跳消息
                }
            }
        }, 0, 50000);  // 每 5 秒发送一次心跳
    }

    private void sendHeartbeat() {
        // 发送心跳消息，可以自定义心跳消息的内容
//        String heartbeatMessage = "{\"type\":\"heartbeat\"}";
        sendPing();
        System.out.println("💓 发送心跳消息");
    }


    private final ConcurrentHashMap<UUID, CompletableFuture<JsonObject>> responseMap = new ConcurrentHashMap<>();
    public JsonObject sendWithResponse(JsonObject object, int timeout) throws ExecutionException, InterruptedException, TimeoutException {

        UUID uuid = UUID.randomUUID();
        //打上 echo标记
        object.addProperty("echo", uuid.toString());
        CompletableFuture<JsonObject> completableFuture = new CompletableFuture<>();
        responseMap.put(uuid, completableFuture);

        this.send(object.getAsString());
        JsonObject jsonObject = completableFuture.get(timeout, TimeUnit.SECONDS);
        return jsonObject;
    }


    public void sendOneWay(JsonObject object,String clientName){

        this.send(object.getAsString());
    }


}
