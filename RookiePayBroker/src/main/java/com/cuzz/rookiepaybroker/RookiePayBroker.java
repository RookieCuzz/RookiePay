package com.cuzz.rookiepaybroker;


import com.cuzz.rookiepaybroker.processors.PayMessageProxy;
import com.cuzz.rookiepaybroker.processors.TryOrderMessageProxy;
import com.cuzz.rookiepaybroker.processors.TryOrderResponseProxy;
import net.afyer.afybroker.server.Broker;
import net.afyer.afybroker.server.plugin.Plugin;

public final class RookiePayBroker extends Plugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Broker.getServer().registerUserProcessor(new PayMessageProxy());
        Broker.getServer().registerUserProcessor(new TryOrderMessageProxy());
        Broker.getServer().registerUserProcessor(new TryOrderResponseProxy());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
