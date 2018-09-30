/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kcwiki.x.cic.proxy;

import com.github.monkeywie.proxyee.exception.HttpProxyExceptionHandle;
import com.github.monkeywie.proxyee.intercept.HttpProxyIntercept;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptInitializer;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptPipeline;
import com.github.monkeywie.proxyee.intercept.common.CertDownIntercept;
import com.github.monkeywie.proxyee.intercept.common.FullRequestIntercept;
import com.github.monkeywie.proxyee.intercept.common.FullResponseIntercept;
import com.github.monkeywie.proxyee.proxy.ProxyConfig;
import com.github.monkeywie.proxyee.server.HttpProxyServer;
import com.github.monkeywie.proxyee.server.HttpProxyServerConfig;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import kcwiki.x.cic.cache.inmem.SystemData;
import kcwiki.x.cic.proxy.intercept.KanColleFilter;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author x5171
 */
public class KanColleHttpProxy {
    
    private static KanColleHttpProxy instance = null;
    private boolean isInit = false;
    private HttpProxyServer httpProxyServer;
    
    private KanColleHttpProxy(){
        
    }
    
    public static KanColleHttpProxy getInstance(){
        if(instance==null){
            instance=new KanColleHttpProxy();
        }
        return instance;
    }
    
    public void InitProxy(HttpProxyServerConfig httpProxyServerConfig, ProxyConfig proxyConfig, KanColleFilter kanColleFilter) {
        if(isInit){
            return;
        }
        httpProxyServer = new HttpProxyServer()
            .serverConfig(httpProxyServerConfig)
            .proxyConfig(proxyConfig)
            .proxyInterceptInitializer(new HttpProxyInterceptInitializer() {
              @Override
              public void init(HttpProxyInterceptPipeline pipeline) {
                pipeline.addLast(new CertDownIntercept());  //处理证书下载
                pipeline.addLast(new HttpProxyIntercept() {
                  @Override
                  public void beforeRequest(Channel clientChannel, HttpRequest httpRequest,
                      HttpProxyInterceptPipeline pipeline) throws Exception {
                    //替换UA，伪装成手机浏览器
                    /*httpRequest.headers().set(HttpHeaderNames.USER_AGENT,
                        "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");*/
                    //转到下一个拦截器处理
                    pipeline.beforeRequest(clientChannel, httpRequest);
                  }

                  @Override
                  public void afterResponse(Channel clientChannel, Channel proxyChannel,
                      HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) throws Exception {

                    //拦截响应，添加一个响应头
//                    httpResponse.headers().remove("Expires");
//                    httpResponse.headers().remove("Pragma");
//                    httpResponse.headers().remove("Cache-Control");
//                    httpResponse.headers().add("Expires", "0");
//                    httpResponse.headers().add("Pragma", "no-cache");
//                    httpResponse.headers().add("Cache-Control", "no-store");
                    
                    //转到下一个拦截器处理
                    pipeline.afterResponse(clientChannel, proxyChannel, httpResponse);
                  }
                });
                pipeline.addLast(new FullResponseIntercept() {

                    @Override
                    public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                      //在匹配到百度首页时插入js
      //                HttpUtil.checkUrl(pipeline.getHttpRequest(), "^dmm.com$");
                        if(!SystemData.isIsReadyIntercept())
                            return false;
                        return kanColleFilter.responseMatch(httpRequest, httpResponse);
                    }

                    @Override
                    public void handelResponse(HttpRequest httpRequest, FullHttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                        kanColleFilter.handelFullResponseMatch(httpRequest, httpResponse);
                    }
                });
                pipeline.addLast(new FullRequestIntercept(){
                    @Override
                    public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                        if(!SystemData.isIsReadyIntercept())
                            return false;
                        return kanColleFilter.requestMatch(httpRequest);
                    }

                    @Override
                    public void handelRequest(FullHttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                        kanColleFilter.handelFullRequestMatch(httpRequest, httpResponse);
                    }
                    
                });
              }
            })
            .httpProxyExceptionHandle(new HttpProxyExceptionHandle() {
              @Override
              public void beforeCatch(Channel clientChannel, Throwable cause) throws Exception {
                  ExceptionUtils.getStackTrace(cause);
                cause.printStackTrace();
                if(clientChannel.isOpen())
                    clientChannel.close();
              }

              @Override
              public void afterCatch(Channel clientChannel, Channel proxyChannel, Throwable cause)
                  throws Exception {
                 ExceptionUtils.getStackTrace(cause);
                cause.printStackTrace();
              }
            });
    }
    
    public void StartProxy(int port){
        httpProxyServer.start(port);
    }
    
    public void CloseProxy(){
        httpProxyServer.close();
        isInit = false;
        instance = null;
    }
    
}
