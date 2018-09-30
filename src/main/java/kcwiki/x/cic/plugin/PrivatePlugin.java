/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kcwiki.x.cic.plugin;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;

/**
 *
 * @author x5171
 */
public abstract class PrivatePlugin extends PublicPlugin {
    
    public abstract boolean isBlock(Channel clientChannel, HttpRequest httpRequest);
    
}
