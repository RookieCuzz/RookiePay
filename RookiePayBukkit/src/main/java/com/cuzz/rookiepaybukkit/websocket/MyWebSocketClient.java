package com.cuzz.rookiepaybukkit.websocket;


import com.cuzz.bukkitmybatis.BukkitMybatis;
import com.cuzz.rookiepaybukkit.RookiePayBukkit;
import com.cuzz.rookiepaybukkit.mapper.OrderProductDOMapper;
import com.cuzz.rookiepaybukkit.mapper.OrdersDOMapper;
import com.cuzz.rookiepaybukkit.model.doo.OrderProductDO;
import com.cuzz.rookiepaybukkit.model.doo.OrderProductDOExample;
import com.cuzz.rookiepaybukkit.model.doo.OrdersDO;
import com.cuzz.rookiepaybukkit.model.doo.OrdersDOExample;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.odalitadevelopments.menus.OdalitaMenus;
import nl.odalitadevelopments.menus.contents.MenuContents;
import nl.odalitadevelopments.menus.items.ClickableItem;
import org.apache.ibatis.session.SqlSession;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;


import java.net.URI;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
//        Gson gson = new Gson();
//        String json = gson.toJson(jsonObject);
//        System.out.println();
        String regex = "ORDER_[a-z0-9]+_[a-z0-9]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jsonObject.get("content").getAsString());
        while (matcher.find()) {
            String orderNumber = matcher.group();
            Bukkit.getLogger().info("Receive order id: " + orderNumber);
            try (SqlSession sqlSession = BukkitMybatis.instance.getSqlSessionFactory().openSession()) {
                OrdersDO order;
                String productName;
                UUID buyerUUID;
                Integer quantity;

                OrdersDOMapper ordersDOMapper = sqlSession.getMapper(OrdersDOMapper.class);
                OrdersDOExample ordersDOExample = new OrdersDOExample();
                ordersDOExample.createCriteria().andOrderNumberEqualTo(orderNumber);
                List<OrdersDO> orders = ordersDOMapper.selectByExample(ordersDOExample);
                if(!orders.isEmpty()) {
                    order = orders.get(0);
                    buyerUUID = UUID.fromString(order.getConsigneeUuid());
                } else {
                    Bukkit.getLogger().warning("Order not found: " + orderNumber);
                    return;
                }

                OrderProductDOMapper orderProductMapper = sqlSession.getMapper(OrderProductDOMapper.class);
                OrderProductDOExample orderProductDOExample = new OrderProductDOExample();
                orderProductDOExample.createCriteria().andBelongOrderNumberEqualTo(orderNumber);
                List<OrderProductDO> orderProductDOS = orderProductMapper.selectByExample(orderProductDOExample);
                if(!orderProductDOS.isEmpty()) {
                    productName = orderProductDOS.get(0).getProductName();
                    quantity = orderProductDOS.get(0).getQuantity();
                } else {
                    Bukkit.getLogger().warning("Order product not found: " + orderNumber);
                    return;
                }

                Player player = Bukkit.getPlayer(buyerUUID);

                assert player != null;
                player.sendMessage("商品发货: " + productName + " 数量: " + quantity);

                OdalitaMenus odalitaMenus = RookiePayBukkit.INSTANCE.getOdalitaMenus();
                MenuContents menuContents = odalitaMenus.getOpenMenuSession(player).getMenuContents();
                menuContents.setTitle("§a支付成功");

                ItemMeta closeItemMeta = new ItemStack(Material.GREEN_WOOL).getItemMeta();
                assert closeItemMeta != null;
                closeItemMeta.setDisplayName("§a完成");
                ItemStack closeItemStack = new ItemStack(Material.GREEN_WOOL);
                closeItemStack.setItemMeta(closeItemMeta);
                ClickableItem closeItem = ClickableItem.of(closeItemStack, event -> {
                    player.closeInventory();
                });
                menuContents.set(8, closeItem);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
//        Bukkit.getOnlinePlayers().forEach(
//                player -> {
//                    Bukkit.getScheduler().runTask(RookiePayBukkit.INSTANCE,
//                        ()->{
//                            player.sendMessage("完成支付"+json);
//                            player.closeInventory();
//                        }
//                    );
//                }
//
//        );

    }


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
                throw new RuntimeException(e);
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
        return completableFuture.get(timeout, TimeUnit.SECONDS);
    }


    public void sendOneWay(JsonObject object,String clientName){

        this.send(object.getAsString());
    }


}
