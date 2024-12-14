package com.cuzz.rookiepaybroker.processors;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.cuzz.rookiepay.RookiePayMessage;
import lombok.Setter;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

import java.util.Collection;

public class PayMessageProxyProcessor extends SyncUserProcessor<RookiePayMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;
    @Override
    public Object handleRequest(BizContext bizContext, RookiePayMessage paySuccessMessage2) throws Exception {
        String playerName = paySuccessMessage2.getPlayer();
        System.out.println(brokerServer.getClientManager().list().size());
        System.out.println(playerName);
        Collection<BrokerPlayer> players = brokerServer.getPlayerManager().getPlayers();
        System.out.println("玩家数量为"+players.size());
        players.forEach(System.out::println);

//        System.out.println(player);
//        player.getServer().oneway(paySuccessMessage2);
        System.out.println("测试处理转发");
        return null;
    }

    @Override
    public String interest() {
        return RookiePayMessage.class.getName();
    }


//    @Override
//    public void setBrokerServer(BrokerServer brokerServer) {
//
//    }
}
