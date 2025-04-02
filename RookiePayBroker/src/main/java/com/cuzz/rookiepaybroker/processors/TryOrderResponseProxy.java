package com.cuzz.rookiepaybroker.processors;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.cuzz.common.rookiepay.RookiePayTryOrderResponse;
import lombok.AllArgsConstructor;
import net.afyer.afybroker.server.Broker;
import net.afyer.afybroker.server.proxy.BrokerPlayer;


@AllArgsConstructor
public class TryOrderResponseProxy extends SyncUserProcessor<RookiePayTryOrderResponse> {
    @Override
    public Object handleRequest(BizContext bizContext, RookiePayTryOrderResponse request){
        if (request ==null){
            return null;
        }

        BrokerPlayer player = Broker.getPlayerManager().getPlayer(request.getBuyer());
        try {
            player.getServer().oneway(request);
        } catch (RemotingException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        return null;
    }

    @Override
    public String interest() {
        return RookiePayTryOrderResponse.class.getName();
    }
}
