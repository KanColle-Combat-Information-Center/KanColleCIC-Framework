/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kcwiki.x.cic.proxy.intercept;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;
import kcwiki.x.cic.cache.inmem.CommunicateFilter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author x5171
 */
public class KanColleFilter {
    private static final Logger LOG = LoggerFactory.getLogger(KanColleFilter.class);
    
    public boolean requestMatch(HttpRequest httpRequest){
        String uri = getAuthorizedUri(httpRequest);
        if(uri == null)
            return false;
        return (CommunicateFilter.PublicPlugins.containsKey(uri) || CommunicateFilter.PrivatePlugins.containsKey(uri));
    }
    
    public boolean responseMatch(HttpRequest httpRequest, HttpResponse httpResponse) {
        String uri = getAuthorizedUri(httpRequest);
        if(uri == null)
            return false;
        return (CommunicateFilter.PublicPlugins.containsKey(uri) || CommunicateFilter.PrivatePlugins.containsKey(uri));
    }
    
    private String getAuthorizedUri(HttpRequest httpRequest){
        String host = httpRequest.headers().get(HttpHeaderNames.HOST);
        if(!CommunicateFilter.getKcServerHostList().contains(host))
            return null;
        String uri = httpRequest.uri();
        if(uri.startsWith("http")){
            uri = httpRequest.uri().replace("http://", "").replace("https://", "");
            uri = uri.substring(uri.indexOf("/")+1);
        }
        if(!uri.contains("/"))
            return null;
        String prefix = uri.substring(0, uri.indexOf("/"));
        if(!CommunicateFilter.getKcServerApiUriList().contains(prefix))
            return null;
        return uri;     
    }
    
    public void handelFullResponseMatch(HttpRequest httpRequest, FullHttpResponse httpResponse) {
        String uri = getAuthorizedUri(httpRequest);
        ByteBuf jsonBuf = httpResponse.content();
        String content = getRealContent(jsonBuf.toString(CharsetUtil.UTF_8));
        if(CommunicateFilter.PrivatePlugins.containsKey(uri)){
            CommunicateFilter.PrivatePlugins.get(uri).forEach(plugin -> {
                try{
                    plugin.callback(uri, content, true);
                } catch (Exception ex) {
                    LOG.error("{}", ExceptionUtils.getStackTrace(ex));
                }
            });
        }
        if(CommunicateFilter.PublicPlugins.containsKey(uri)){
            CommunicateFilter.PublicPlugins.get(uri).forEach(plugin -> {
                try{
                    plugin.callback(uri, content, true);
                } catch (Exception ex) {
                    LOG.error("{}", ExceptionUtils.getStackTrace(ex));
                }
            });
        }
    }
    
    public void handelRequestMatch(HttpRequest httpRequest) {
        String uri = httpRequest.uri().substring(1);
        String content = "";
        if (httpRequest instanceof FullHttpRequest) {
            ByteBuf jsonBuf = ((FullHttpRequest)httpRequest).content();
            content = getRealContent(jsonBuf.toString(CharsetUtil.UTF_8));
        }
        if(CommunicateFilter.PrivatePlugins.containsKey(uri)){
            String _content = content;
            CommunicateFilter.PrivatePlugins.get(uri).forEach(plugin -> {
                try{
                    plugin.callback(uri, _content, false);
                } catch (Exception ex) {
                    LOG.error("{}", ExceptionUtils.getStackTrace(ex));
                }
            });
        }
        if(CommunicateFilter.PublicPlugins.containsKey(uri)){
            String _content = content;
            CommunicateFilter.PublicPlugins.get(uri).forEach(plugin -> {
                try{
                    plugin.callback(uri, _content, false);
                } catch (Exception ex) {
                    LOG.error("{}", ExceptionUtils.getStackTrace(ex));
                }
            });
        }
    }
    
    public void handelFullRequestMatch(FullHttpRequest httpRequest, HttpResponse httpResponse) {
        String uri = httpRequest.uri().substring(1);
        ByteBuf jsonBuf = httpRequest.content();
        String content = getRealContent(jsonBuf.toString(CharsetUtil.UTF_8));
        if(CommunicateFilter.PrivatePlugins.containsKey(uri)){
            CommunicateFilter.PrivatePlugins.get(uri).forEach(plugin -> {
                try{
                    plugin.callback(uri, content, false);
                } catch (Exception ex) {
                    LOG.error("{}", ExceptionUtils.getStackTrace(ex));
                }
            });
        }
        if(CommunicateFilter.PublicPlugins.containsKey(uri)){
            CommunicateFilter.PublicPlugins.get(uri).forEach(plugin -> {
                try{
                    plugin.callback(uri, content, false);
                } catch (Exception ex) {
                    LOG.error("{}", ExceptionUtils.getStackTrace(ex));
                }
            });
        }
    }
    
    private String getRealContent(String content){
        content = content.replace("svdata=", "");
        return content;
    }
    
}
