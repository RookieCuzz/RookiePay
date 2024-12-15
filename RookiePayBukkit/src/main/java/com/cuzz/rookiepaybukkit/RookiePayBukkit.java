package com.cuzz.rookiepaybukkit;

import com.cuzz.rookiepaybukkit.model.Info;
import com.cuzz.rookiepaybukkit.processors.PayMsgProcessor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import okhttp3.*;
import java.io.IOException;

public final class RookiePayBukkit extends JavaPlugin {

    @Override
    public void onLoad(){
        Broker.buildAction(this::buildBroker);
    }

    private void buildBroker(BrokerClientBuilder builder){
        builder.registerUserProcessor(new PayMsgProcessor());
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("MinePay 插件已启动");
        this.getCommand("minepay").setExecutor(new MinePayCommand());
    }

    public String getQRCodeStr(Info info){

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
                .url("https://www.4399mc.cn/cuzz/order/createOrder")
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
        return "";
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public class MinePayCommand implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            // 检查命令是否是 /minepay
            if (command.getName().equalsIgnoreCase("minepay")) {
                // 确保发送者是玩家
                if (sender instanceof Player) {
                    Player player = (Player) sender;

                    // 确保玩家输入了金额
                    if (args.length != 1) {
                        player.sendMessage("请提供一个金额！使用方法: /minepay <金额>");
                        return false;
                    }

                    // 尝试解析玩家输入的金额
                    try {
                        Double amount = Double.parseDouble(args[0]);

                        if (amount <= 0) {
                            player.sendMessage("金额必须是一个大于0的数！");
                            return false;
                        }


                        //重点在 谁花多少钱买什么
                        String uuid = player.getUniqueId().toString();
                        String name = player.getName();
                        Info info = new Info();
                        info.setDescription("购买点卷"+amount+"元");
                        info.setBuyerName(name);
                        info.setUserName(name);
                        info.setUserUUID(uuid);
                        info.setMoney(amount);
                        info.setProductAmount(-1);
                        info.setPayment("wechatpay");


                        System.out.println(info);
                        String qrCodeStr = getQRCodeStr(info);
                        Inventory virtualChest = Bukkit.createInventory(null, 54, "      "+qrCodeStr);
                        player.openInventory(virtualChest);

                    } catch (NumberFormatException e) {
                        player.sendMessage("请输入一个有效的数字金额！");
                        return false;
                    }
                } else {
                    sender.sendMessage("只有玩家可以使用此命令！");
                    return false;
                }
            }
            return true;
        }
    }
}
