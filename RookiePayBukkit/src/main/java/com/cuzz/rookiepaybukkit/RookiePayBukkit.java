package com.cuzz.rookiepaybukkit;

import com.cuzz.bukkitmybatis.utils.MapperRegister;
import com.cuzz.common.rookiepay.RookiePayTryOrderMessage;
import com.cuzz.rookiepaybukkit.commands.MinePayCmd;
import com.cuzz.rookiepaybukkit.model.Info;
import com.cuzz.rookiepaybukkit.processors.PayMsgProcessor;
import com.cuzz.rookiepaybukkit.processors.TryOrderResponseProcessor;
import com.cuzz.rookiepaybukkit.websocket.MyWebSocketClient;
import nl.odalitadevelopments.menus.OdalitaMenus;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.afyer.afybroker.client.Broker;
import net.afyer.afybroker.client.BrokerClientBuilder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class RookiePayBukkit extends JavaPlugin {

    public static RookiePayBukkit INSTANCE;
    public static  MyWebSocketClient myWebSocketClient = null;
    public static String url="ws://103.205.253.165:28088/web?key=botpwd&client=*bukkit";
    OdalitaMenus odalitaMenus;

    @Override
    public void onLoad(){
        Broker.buildAction(this::buildBroker);
    }

    private void buildBroker(BrokerClientBuilder builder){
        builder.registerUserProcessor(new PayMsgProcessor());
        builder.registerUserProcessor(new TryOrderResponseProcessor());

    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("MinePay 插件已启动");
        MapperRegister.registerMappers(this);
        this.getCommand("minepay").setExecutor(new MinePayCmd());
        RookiePayBukkit.INSTANCE =this;


        try {
            myWebSocketClient = new MyWebSocketClient(new URI(url));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        myWebSocketClient.connect();
//        this.getCommand("rookiepay").setExecutor(new MinePayCommand());

        odalitaMenus = OdalitaMenus.createInstance(this);
    }

    public OdalitaMenus getOdalitaMenus(){
        return odalitaMenus;
    }

    Map<UUID, Boolean> playerPaymentStatus = new HashMap<>();

    public Map<UUID, Boolean> getPlayerPaymentStatus() {
        return playerPaymentStatus;
    }

    public String getQRCodeStr(RookiePayTryOrderMessage rookiePayTryOrderMessage){
        String name = rookiePayTryOrderMessage.getBuyerName();
        String uuid = rookiePayTryOrderMessage.getUserUUID();
        Double money = rookiePayTryOrderMessage.getMoney();
        Info info = new Info();
        info.setDescription(rookiePayTryOrderMessage.getDescription());
        info.setBuyerName(name);
        info.setUserName(name);
        info.setUserUUID(uuid);
        info.setMoney(money);
        info.setProductAmount(rookiePayTryOrderMessage.getProductAmount());
        info.setPayment("wechatpay");
        List<String> productList = rookiePayTryOrderMessage.getProductList();
        info.setProductList(productList);
        OkHttpClient client = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            // 将对象序列化为 JSON 字符串
            json= objectMapper.writeValueAsString(info);
            System.out.println("对象的 JSON 表示: " + json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // 创建请求体（比如发送 JSON 数据）
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url("https://www.4399mc.cn:20443/cuzz/order/createOrder")
                .post(body)
                .build();

        // 执行请求
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将 JSON 字符串解析为 JsonNode
                JsonNode rootNode = objectMapper.readTree(response.body().string());

                // 获取 "data" 节点
                JsonNode dataNode = rootNode.get("data");

                // 从 "data" 中获取 "content"
                String qrcode = dataNode.get("content").asText();
                return qrcode;
            } else {
                System.out.println("请求失败: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    @Override
    public void onDisable() {

        MapperRegister.unregisterMapper(this);
        // Plugin shutdown logic
    }


}
