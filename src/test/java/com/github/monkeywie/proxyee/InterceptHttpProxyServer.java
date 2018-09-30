package com.github.monkeywie.proxyee;

import com.github.monkeywie.proxyee.exception.HttpProxyExceptionHandle;
import com.github.monkeywie.proxyee.intercept.HttpProxyIntercept;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptInitializer;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptPipeline;
import com.github.monkeywie.proxyee.intercept.common.CertDownIntercept;
import com.github.monkeywie.proxyee.intercept.common.FullRequestIntercept;
import com.github.monkeywie.proxyee.intercept.common.FullResponseIntercept;
import com.github.monkeywie.proxyee.proxy.ProxyConfig;
import com.github.monkeywie.proxyee.proxy.ProxyType;
import com.github.monkeywie.proxyee.server.HttpProxyServer;
import com.github.monkeywie.proxyee.server.HttpProxyServerConfig;
import com.github.monkeywie.proxyee.util.HttpUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;

/**
 * 
 * @author x5171
 * https://www.jianshu.com/p/ef4a71d2404d
 * https://fangjian0423.github.io/2016/08/29/netty-in-action-note2/
 * https://blog.csdn.net/suifeng3051/article/details/22800171
 * https://www.cnblogs.com/lzcys8868/p/6281932.html
 * https://www.cnblogs.com/littleatp/p/5878763.html
 * https://www.jianshu.com/p/b9f3f6a16911
 * https://blog.csdn.net/tanga842428/article/details/52463248
 * https://blog.csdn.net/gaowenhui2008/article/details/55044704
 * http://shift-alt-ctrl.iteye.com/blog/2219057
 */

public class InterceptHttpProxyServer {
    static final Logger LOG = Logger.getLogger(InterceptHttpProxyServer.class.getName());
    
    FileWriter fw = null;
    
    @Test
    public void test() {
        HttpProxyServerConfig config =  new HttpProxyServerConfig();
        config.setHandleSsl(true);
        config.setProxyGroupThreads(1);
        config.setBossGroupThreads(1);
        config.setWorkerGroupThreads(1);
        new HttpProxyServer()
            .serverConfig(config)
            .proxyConfig(new ProxyConfig(ProxyType.HTTP, "127.0.0.1", 8123))  //使用socks5二级代理
            .proxyInterceptInitializer(new HttpProxyInterceptInitializer() {
              @Override
              public void init(HttpProxyInterceptPipeline pipeline) {
                pipeline.addLast(new CertDownIntercept());  //处理证书下载
                pipeline.addLast(new HttpProxyIntercept() {
                  @Override
                  public void beforeRequest(Channel clientChannel, HttpRequest httpRequest,
                      HttpProxyInterceptPipeline pipeline) throws Exception {
                      if (httpRequest instanceof FullHttpRequest) {
                          FullHttpRequest fullHttpRequest = (FullHttpRequest) httpRequest;
                          String body = fullHttpRequest.content().toString(CharsetUtil.UTF_8);
                          System.out.print(true);
                      }
                    //替换UA，伪装成手机浏览器
                    /*httpRequest.headers().set(HttpHeaderNames.USER_AGENT,
                        "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");*/
                    //转到下一个拦截器处理
                    pipeline.beforeRequest(clientChannel, httpRequest);
                  }

                  @Override
                  public void afterResponse(Channel clientChannel, Channel proxyChannel,
                      HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) throws Exception {
                      if (httpResponse instanceof FullHttpResponse) {
                          FullHttpResponse fullHttpResponse = (FullHttpResponse) httpResponse;
                          String body = fullHttpResponse.content().toString(CharsetUtil.UTF_8);
                          System.out.print(true);
                      }
                    //拦截响应，添加一个响应头
//                    httpResponse.headers().add("intercept", "test");
                    //转到下一个拦截器处理
                    pipeline.afterResponse(clientChannel, proxyChannel, httpResponse);
                  }
                });
                pipeline.addLast(new FullResponseIntercept() {

                    @Override
                    public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                      //在匹配到百度首页时插入js
      //                HttpUtil.checkUrl(pipeline.getHttpRequest(), "^dmm.com$");
                      return (HttpUtil.checkUrl(pipeline.getHttpRequest(), "^203.104.209.102.+") || HttpUtil.checkUrl(pipeline.getHttpRequest(), "^125.6.189.39.+"))
                          && httpRequest.uri().contains("kcsapi");
//                        return httpRequest.uri().contains("kcsapi");
                    }

                    @Override
                    public void handelResponse(HttpRequest httpRequest, FullHttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                      //打印原始响应信息
//                      LOG.log(Level.INFO, httpResponse.toString());
//                      LOG.log(Level.INFO, httpResponse.content().toString(Charset.defaultCharset()));
//                      System.out.println(httpResponse.toString());
//                      System.out.println(httpResponse.content().toString(Charset.defaultCharset()));
                      //修改响应头和响应体
                      httpResponse.headers().set("custom-header", "inject success");
                      /*int index = ByteUtil.findText(httpResponse.content(), "<head>");
                      ByteUtil.insertText(httpResponse.content(), index, "<script>alert(1)</script>");*/
      //                httpResponse.content().writeBytes("<script>alert('hello proxyee')</script>".getBytes());
                      ByteBuf jsonBuf = httpResponse.content();
                      String body = jsonBuf.toString(CharsetUtil.UTF_8);
                      try {
                        //如果文件存在，则追加内容；如果文件不存在，则创建文件
                        if(StringUtils.isBlank(body) || body.contains("bad-request")){
                            System.err.print(body);
                        }
                        File f=new File("E:\\test.txt");
                        fw = new FileWriter(f, true);
                        } catch (IOException e) {
                        e.printStackTrace();
                        }
                        PrintWriter pw = new PrintWriter(fw);
                        pw.println(httpRequest.uri());
                        pw.println(body);
                        pw.println("\r\n");
                        pw.flush();
                        try {
                        fw.flush();
                        pw.close();
                        fw.close();
                      } catch (IOException e) {
                            e.printStackTrace();
                      }
                    }
                });
                pipeline.addLast(new FullRequestIntercept(){
                    @Override
                    public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                        if(!HttpUtil.checkUrl(httpRequest, "^203.104.209.102.+") && !HttpUtil.checkUrl(httpRequest, "^125.6.189.39.+"))
                            return false;
                        String _url = httpRequest.uri();
                        String _suffix = _url.substring(_url.lastIndexOf("/"));
                        return (!_suffix.contains(".") && httpRequest.uri().contains("kcsapi"));
                    }

                    @Override
                    public void handelRequest(FullHttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
//                        httpRequest.headers().set("custom-header", "inject success");
                      /*int index = ByteUtil.findText(httpResponse.content(), "<head>");
                      ByteUtil.insertText(httpResponse.content(), index, "<script>alert(1)</script>");*/
      //                httpResponse.content().writeBytes("<script>alert('hello proxyee')</script>".getBytes());
                      ByteBuf jsonBuf = httpRequest.content();
                      String body = jsonBuf.toString(CharsetUtil.UTF_8);
                      try {
                        //如果文件存在，则追加内容；如果文件不存在，则创建文件
                        if(StringUtils.isBlank(body) || body.contains("bad-request")){
                            System.err.print(body);
                        }
                        File f=new File("E:\\test.txt");
                        fw = new FileWriter(f, true);
                        } catch (IOException e) {
                        e.printStackTrace();
                        }
                        PrintWriter pw = new PrintWriter(fw);
                        pw.println(httpRequest.uri());
                        pw.println(body);
                        pw.println("\r\n");
                        pw.flush();
                        try {
                        fw.flush();
                        pw.close();
                        fw.close();
                      } catch (IOException e) {
                            e.printStackTrace();
                      }
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
            })
            .start(9999);
    }
   
}
