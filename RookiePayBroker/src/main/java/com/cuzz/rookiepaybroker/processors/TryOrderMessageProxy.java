package com.cuzz.rookiepaybroker.processors;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.cuzz.common.rookiepay.RookiePayTryOrderMessage;
import net.afyer.afybroker.server.Broker;
import net.afyer.afybroker.server.proxy.BrokerClientItem;

public class TryOrderMessageProxy extends SyncUserProcessor<RookiePayTryOrderMessage> {


//    @Override
//    public void handleRequest(BizContext bizContext, AsyncContext asyncContext, RookiePayTryOrderMessage rookiePayTryOrderMessage) throws Exception {
//
//    }

    @Override
    public Object handleRequest(BizContext bizContext, RookiePayTryOrderMessage rookiePayTryOrderMessage) throws Exception {

        BrokerClientItem springx = Broker.getClient("springx");
        springx.oneway(rookiePayTryOrderMessage);

        return null;
    }

    @Override
    public String interest() {
        return RookiePayTryOrderMessage.class.getName();
    }
}
