package com.github.monkeywie.proxyee;

import com.github.monkeywie.proxyee.proxy.ProxyConfig;
import com.github.monkeywie.proxyee.proxy.ProxyType;
import com.github.monkeywie.proxyee.server.HttpProxyServer;
import com.github.monkeywie.proxyee.server.HttpProxyServerConfig;
import org.junit.Test;

public class NormalHttpProxyServer {

//    @Test
    public void test() {
        HttpProxyServerConfig config =  new HttpProxyServerConfig();
    config.setBossGroupThreads(1);
    config.setWorkerGroupThreads(1);
    config.setProxyGroupThreads(1);
    new HttpProxyServer()
        .proxyConfig(new ProxyConfig(ProxyType.HTTP, "127.0.0.1", 8888))
        .serverConfig(config)
        .start(9999);
    }
    
    
  public static void main(String[] args) throws Exception {
   //new HttpProxyServer().start(9998);

    HttpProxyServerConfig config =  new HttpProxyServerConfig();
    config.setBossGroupThreads(1);
    config.setWorkerGroupThreads(1);
    config.setProxyGroupThreads(1);
    new HttpProxyServer()
         .proxyConfig(new ProxyConfig(ProxyType.HTTP, "127.0.0.1", 8123))
        .serverConfig(config)
        .start(9999);
  }
}
