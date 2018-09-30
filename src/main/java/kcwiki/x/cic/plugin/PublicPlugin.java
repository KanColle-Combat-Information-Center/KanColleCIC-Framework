/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kcwiki.x.cic.plugin;

/**
 *
 * @author x5171
 */
public abstract class PublicPlugin extends BasePlugin {
    
    public abstract void callback(String uri, String content, boolean isResponse);
    
    public abstract void init();
    
}
