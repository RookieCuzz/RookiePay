package com.cuzz.rookiepaybroker;


import com.cuzz.rookiepaybroker.processors.PayMessageProxyProcessor;
import net.afyer.afybroker.server.Broker;
import net.afyer.afybroker.server.plugin.Plugin;

import javax.swing.*;

public final class RookiePayBroker extends Plugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Broker.getServer().registerUserProcessor(new PayMessageProxyProcessor());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
