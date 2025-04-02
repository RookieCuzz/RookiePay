package com.cuzz.rookiepaybroker.processors;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.cuzz.common.rookiepay.RookiePaySuccessMessage;
import lombok.Setter;
import net.afyer.afybroker.server.BrokerServer;
import net.afyer.afybroker.server.aware.BrokerServerAware;
import net.afyer.afybroker.server.proxy.BrokerPlayer;

public class PayMessageProxy extends SyncUserProcessor<RookiePaySuccessMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;
    @Override
    public Object handleRequest(BizContext bizContext, RookiePaySuccessMessage paySuccessMessage2) throws Exception {
        String playerName = paySuccessMessage2.getPlayer();
        BrokerPlayer player = brokerServer.getPlayerManager().getPlayer(playerName);
        if (player == null){
            throw new Exception("玩家不在线,发货逻辑需要改变");
        }
        //执行发货不应该在游戏后端,因为发货到邮箱这个操作可以直接Spring后端完成
        //否则若玩家不在线 就无法正常发货了
        //这里通知玩家付款成功,即将完成发货

        //这里一个是单纯通知玩家
        player.getServer().oneway(paySuccessMessage2);
        System.out.println("测试处理转发");
        return null;
    }

    @Override
    public String interest() {
        return RookiePaySuccessMessage.class.getName();
    }


//    @Override
//    public void setBrokerServer(BrokerServer brokerServer) {
//
//    }
}
