package com.github.monkeywie.proxyee.intercept.common;

import com.github.monkeywie.proxyee.intercept.HttpProxyIntercept;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptPipeline;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public abstract class FullRequestIntercept extends HttpProxyIntercept {

  //default max content length size is 8MB
  private static final int defaultMaxContentLength = 1024 * 1024 * 8;

  private int maxContentLength;

  public FullRequestIntercept() {
    this(defaultMaxContentLength);
  }

  public FullRequestIntercept(int maxContentLength) {
    this.maxContentLength = maxContentLength;
  }

  @Override
  public final void beforeRequest(Channel clientChannel, HttpRequest httpRequest,
      HttpProxyInterceptPipeline pipeline) throws Exception {
    if (httpRequest instanceof FullHttpRequest) {
      FullHttpRequest fullHttpRequest = (FullHttpRequest) httpRequest;
      handelRequest(fullHttpRequest, pipeline.getHttpResponse(), pipeline);
      if (fullHttpRequest.headers().contains(HttpHeaderNames.CONTENT_LENGTH)) {
        fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpRequest.content().readableBytes());
      }
      if(clientChannel.pipeline().get("decompress") != null)
        clientChannel.pipeline().remove("decompress");
      if(clientChannel.pipeline().get("aggregator") != null)
        clientChannel.pipeline().remove("aggregator");
    } else if (match(pipeline.getHttpRequest(), pipeline.getHttpResponse(), pipeline)) {
      pipeline.resetAfterHead();
      clientChannel.pipeline().addAfter("httpCodec", "decompress", new HttpContentDecompressor());
      clientChannel.pipeline()
          .addAfter("decompress", "aggregator", new HttpObjectAggregator(maxContentLength));
      clientChannel.pipeline().fireChannelRead(httpRequest);
      return;
    }
    pipeline.beforeRequest(clientChannel, httpRequest);
  }

  protected boolean isHtml(HttpRequest httpRequest, HttpResponse httpResponse) {
    String accept = httpRequest.headers().get(HttpHeaderNames.ACCEPT);
    String contentType = httpResponse.headers().get(HttpHeaderNames.CONTENT_TYPE);
    return httpResponse.status().code() == 200 && accept != null && accept
        .matches("^.*text/html.*$") && contentType != null && contentType
        .matches("^text/html.*$");
  }
  
  protected boolean isJson(HttpRequest httpRequest, HttpResponse httpResponse) {
    String accept = httpRequest.headers().get(HttpHeaderNames.ACCEPT);
    String contentType = httpResponse.headers().get(HttpHeaderNames.CONTENT_TYPE);
    String _url = httpRequest.uri();
    String _suffix = _url.substring(_url.lastIndexOf("/"));
    if(_suffix.contains("."))
        return false;
    return httpResponse.status().code() == 200 && accept != null && accept
        .matches("^.*application/json.*$") && contentType != null && contentType
        .matches("^.*application.*$|^.*text/plain.*$") ;
  }

  /**
   * 匹配到的响应会解码成FullResponse
   */
  public abstract boolean match(HttpRequest httpRequest, HttpResponse httpResponse,
      HttpProxyInterceptPipeline pipeline);

  /**
   * 拦截并处理响应
   */
  public abstract void handelRequest(FullHttpRequest httpRequest, HttpResponse httpResponse,
      HttpProxyInterceptPipeline pipeline);
}
