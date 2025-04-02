package com.cuzz.rookiepaybukkit.processors;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.cuzz.common.rookiepay.RookiePayTryOrderResponse;
import com.cuzz.rookiepaybukkit.RookiePayBukkit;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


@AllArgsConstructor
public class TryOrderResponseProcessor extends SyncUserProcessor<RookiePayTryOrderResponse> {
    @Override
    public Object handleRequest(BizContext bizContext, RookiePayTryOrderResponse request){
        if (request ==null){
            return null;
        }
        String playerName = request.getBuyer();
        String orderId = request.getOrderId();
        Player player = Bukkit.getPlayer(playerName);

        Bukkit.getServer().getScheduler().runTask(RookiePayBukkit.INSTANCE, new BukkitRunnable() {
            @Override
            public void run() {

                player.sendMessage(request.getQrcode());

            }
        });


        return null;
    }

    @Override
    public String interest() {
        return RookiePayTryOrderResponse.class.getName();
    }
}
