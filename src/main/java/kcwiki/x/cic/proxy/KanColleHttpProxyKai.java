/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kcwiki.x.cic.proxy;

import com.github.monkeywie.proxyee.proxy.ProxyConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Queue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;
import kcwiki.x.cic.cache.inmem.SystemData;
import kcwiki.x.cic.proxy.intercept.KanColleFilter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.littleshoot.proxy.ActivityTracker;
import org.littleshoot.proxy.ActivityTrackerAdapter;
import org.littleshoot.proxy.ChainedProxy;
import org.littleshoot.proxy.ChainedProxyAdapter;
import org.littleshoot.proxy.ChainedProxyManager;
import org.littleshoot.proxy.FullFlowContext;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.TransportProtocol;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.littleshoot.proxy.impl.ProxyThreadPools;
import org.littleshoot.proxy.impl.ProxyUtils;
import org.littleshoot.proxy.impl.ThreadPoolConfiguration;
import org.littleshoot.proxy.mitm.CertificateSniffingMitmManager;
import org.littleshoot.proxy.mitm.RootCertificateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author x5171
 */
public class KanColleHttpProxyKai {
    private static final Logger LOG = LoggerFactory.getLogger(KanColleHttpProxyKai.class);
    
    
    private static KanColleHttpProxyKai instance = null;
    private boolean isInit = false;
    private HttpProxyServerBootstrap httpProxyServerBootstrap;
    private HttpProxyServer httpProxyServer;
    private ProxyConfig proxyConfig;
    private KanColleFilter kanColleFilter;
    
    private static final AttributeKey<String> CONNECTED_URL = AttributeKey.valueOf("connected_url");
    
    protected final AtomicLong REQUESTS_SENT_BY_DOWNSTREAM = new AtomicLong(
            0l);
    protected final AtomicLong REQUESTS_RECEIVED_BY_UPSTREAM = new AtomicLong(
            0l);
    protected final ConcurrentSkipListSet<TransportProtocol> TRANSPORTS_USED = new ConcurrentSkipListSet<TransportProtocol>();

    protected final ActivityTracker DOWNSTREAM_TRACKER = new ActivityTrackerAdapter() {
        @Override
        public void requestSentToServer(FullFlowContext flowContext,
                io.netty.handler.codec.http.HttpRequest httpRequest) {
            REQUESTS_SENT_BY_DOWNSTREAM.incrementAndGet();
            TRANSPORTS_USED.add(flowContext.getChainedProxy()
                    .getTransportProtocol());
        }
    };
    
    private KanColleHttpProxyKai(){
        
    }
    
    public static KanColleHttpProxyKai getInstance(){
        if(instance==null){
            instance=new KanColleHttpProxyKai();
        }
        return instance;
    }
    
    public void InitProxy(ProxyConfig proxyConfig, KanColleFilter kanColleFilter, int listenPort) throws RootCertificateException {
        if(isInit){
            return;
        }
        this.proxyConfig = proxyConfig;
        this.kanColleFilter = kanColleFilter;
        
        ThreadPoolConfiguration threadPoolConfiguration = new ThreadPoolConfiguration()
                .withAcceptorThreads(12)
                .withClientToProxyWorkerThreads(12)
                .withProxyToServerWorkerThreads(12);
        
        this.httpProxyServerBootstrap = DefaultHttpProxyServer
                .bootstrap()
//            .bootstrapFromFile("./littleproxy.properties")
            .withAllowLocalOnly(true)
            .withName("KcsCICProxy")
//            .withProxyAlias("")
            .withPort(listenPort)
//            .withThreadPoolConfiguration(threadPoolConfiguration)
            .withChainProxyManager(chainedProxyManager())
//            .plusActivityTracker(DOWNSTREAM_TRACKER)
            .withManInTheMiddle(new CertificateSniffingMitmManager())
            .withFiltersSource(new HttpFiltersSourceAdapter() {
                
                @Override
                public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext clientCtx) {
                    String uri = originalRequest.uri();
                    if (originalRequest.method()== HttpMethod.CONNECT) {
                        if (clientCtx != null) {
                            String prefix = "https://" + uri.replaceFirst(":443$", "");
                            clientCtx.channel().attr(CONNECTED_URL).set(prefix);
                        }
                        return new HttpFiltersAdapter(originalRequest, clientCtx);
                    }
                    String connectedUrl = clientCtx.channel().attr(CONNECTED_URL).get();
                    if (connectedUrl == null) {
                        return genFilterRequestAdapter(originalRequest, uri);
                    }
                    return genFilterRequestAdapter(originalRequest, connectedUrl + uri);
                }
                
                @Override
                public int getMaximumRequestBufferSizeInBytes() {
                    return 1024 * 1024;
                }
                @Override
                public int getMaximumResponseBufferSizeInBytes() {
                    return 10 * 1024 * 1024;
                }
            });
    }
    
    private HttpFilters genFilterRequestAdapter(HttpRequest originalRequest, String url) {
        return new HttpFiltersAdapter(originalRequest) {
                        @Override
                        public HttpResponse clientToProxyRequest(
                                HttpObject httpObject) {
                            if(SystemData.isIsReadyIntercept()){
                                if (httpObject instanceof HttpRequest) {
                                    HttpRequest httpRequest = (HttpRequest) httpObject;
                                    try{
                                    if(kanColleFilter.requestMatch(httpRequest))
                                        
                                            kanColleFilter.handelRequestMatch(httpRequest);
                                        } catch (Exception ex) {
                                            LOG.error(ExceptionUtils.getStackTrace(ex));
                                        }
//                                    if(false){
//                                        String body = "Bad Request to URI: " + httpRequest.uri();
//                                        FullHttpResponse response = ProxyUtils.createFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, body);
//                                        if (ProxyUtils.isHEAD(httpRequest)) {
//                                            response.content().clear();
//                                        }
//                                        return response;
//                                    }
                                }
                            }
                            
                            return null;
                        }

                        @Override
                        public HttpObject proxyToClientResponse(
                                HttpObject httpObject) {
                            if(SystemData.isIsReadyIntercept()){
                                if (httpObject instanceof HttpResponse) {
                                    try{
                                    if(kanColleFilter.responseMatch(originalRequest, (HttpResponse) httpObject))
                                        if (httpObject instanceof FullHttpResponse) 
                                            kanColleFilter.handelFullResponseMatch(originalRequest, (FullHttpResponse) httpObject);
                                                
                                    } catch (Exception ex) {
                                                LOG.error(ExceptionUtils.getStackTrace(ex));
                                    }
                                        
                                }
                            }
                            return httpObject;
                        }

                        @Override
                        public HttpResponse proxyToServerRequest(
                                HttpObject httpObject) {
                            return null;
                        }
                        @Override
                        public HttpObject serverToProxyResponse(
                                HttpObject httpObject) {
                            return httpObject;
                        }
                    };
    }
    
    protected ChainedProxyManager chainedProxyManager() {
        return new ChainedProxyManager() {
            @Override
            public void lookupChainedProxies(HttpRequest httpRequest,
                    Queue<ChainedProxy> chainedProxies) {
                chainedProxies.add(newChainedProxy());
            }
        };
    }
    
    protected ChainedProxy newChainedProxy() {
        return new BaseChainedProxy();
    }
    
    protected class BaseChainedProxy extends ChainedProxyAdapter {
        @Override
        public InetSocketAddress getChainedProxyAddress() {
            try {
                return new InetSocketAddress(
                        InetAddress.getByName(proxyConfig.getHost()), 
                        proxyConfig.getPort());
            } catch (UnknownHostException uhe) {
                throw new RuntimeException(
                        "Unable to resolve 127.0.0.1?!");
            }
        }
    }
    
    public void StartProxy(){
        this.httpProxyServer = httpProxyServerBootstrap.start();
        waitUntilInterupted();
    }
    
    public void CloseProxy(){
        this.httpProxyServer.stop();
        isInit = false;
        instance = null;
    }
    
    protected void tearDown() throws Exception {
        this.httpProxyServer.abort();
    }
    
    public static void waitUntilInterupted() {
        new Thread() {
            @Override
            public void run() {
                for (;;) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignore) {
                        break;
                    }
                }
            }
        }.run();
    }
}
