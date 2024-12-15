package com.cuzz.rookiepaybukkit.processors;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.cuzz.common.rookiepay.RookiePaySuccessMessage;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


@AllArgsConstructor
public class PayMsgProcessor extends SyncUserProcessor<RookiePaySuccessMessage> {
    @Override
    public Object handleRequest(BizContext bizContext, RookiePaySuccessMessage request){
        if (request ==null){
            return null;
        }
        String playerName = request.getPlayer();
        String orderId = request.getOrderId();

        Player player = Bukkit.getPlayer(playerName);
        player.sendMessage("你的订单:" +orderId+ "成功支付!");

        player.sendMessage("但是我跑路了 A.A");
        return null;
    }

    @Override
    public String interest() {
        return RookiePaySuccessMessage.class.getName();
    }
}
