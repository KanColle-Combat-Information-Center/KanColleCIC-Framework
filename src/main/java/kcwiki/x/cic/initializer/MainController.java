/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kcwiki.x.cic.initializer;

import com.github.monkeywie.proxyee.proxy.ProxyConfig;
import com.github.monkeywie.proxyee.proxy.ProxyType;
import com.github.monkeywie.proxyee.server.HttpProxyServerConfig;
import java.util.logging.Level;
import java.util.logging.Logger;
import kcwiki.x.cic.proxy.KanColleHttpProxy;
import kcwiki.x.cic.proxy.KanColleHttpProxyKai;
import kcwiki.x.cic.proxy.intercept.KanColleFilter;
import org.littleshoot.proxy.mitm.RootCertificateException;

/**
 *
 * @author x5171
 */
public class MainController {
    
    public void startupProxy(int port){
        ProxyConfig proxyConfig = new ProxyConfig(ProxyType.HTTP, "127.0.0.1", 8123);
//        HttpProxyServerConfig httpProxyServerConfig =  new HttpProxyServerConfig();
//        httpProxyServerConfig.setHandleSsl(true);
//        httpProxyServerConfig.setBossGroupThreads(1);
//        httpProxyServerConfig.setWorkerGroupThreads(1);
//        KanColleHttpProxy proxy = KanColleHttpProxy.getInstance();
//        proxy.InitProxy(httpProxyServerConfig, proxyConfig, new KanColleFilter());
//        proxy.StartProxy(port);
        KanColleHttpProxyKai proxy = KanColleHttpProxyKai.getInstance();
        try {
            proxy.InitProxy(proxyConfig, new KanColleFilter(), port);
            proxy.StartProxy();
        } catch (RootCertificateException ex) {
            
        }
    }
    
}
