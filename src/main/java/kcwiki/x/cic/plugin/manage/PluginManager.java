/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kcwiki.x.cic.plugin.manage;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import kcwiki.x.cic.cache.inmem.CommunicateFilter;
import kcwiki.x.cic.plugin.PrivatePlugin;
import kcwiki.x.cic.plugin.PublicPlugin;

/**
 *
 * @author x5171
 */
public class PluginManager {
    private SecureRandom random = new SecureRandom();
    
    public boolean loadPlugin(PrivatePlugin privatePlugin) {
        List<String> uris = getRealUri(privatePlugin.getPluginInterceptUri());
        if(uris.isEmpty()){
            return false;
        }
        String id = UUID.randomUUID().toString();
        privatePlugin.setID(id);
        uris.forEach(uri -> {
            List<PrivatePlugin> _list = CommunicateFilter.PrivatePlugins.get(uri);
            if(_list == null){
                _list = new ArrayList();
            }
            _list.add(privatePlugin);
            CommunicateFilter.PrivatePlugins.put(uri, _list);
        });
        privatePlugin.init();
        return true;
    }
    
    public boolean loadPlugin(PublicPlugin publicPlugin) {
        List<String> uris = getRealUri(publicPlugin.getPluginInterceptUri());
        if(uris.isEmpty()){
            return false;
        }
        String id = UUID.randomUUID().toString();
        publicPlugin.setID(id);
        uris.forEach(uri -> {
            List<PublicPlugin> _list = CommunicateFilter.PublicPlugins.get(uri);
            if(_list == null){
                _list = new ArrayList();
            }
            _list.add(publicPlugin);
            CommunicateFilter.PublicPlugins.put(uri, _list);
        });
        publicPlugin.init();
        return true;
    }
    
    public boolean unloadPlugin(PrivatePlugin privatePlugin) {
        String id = privatePlugin.getID();
        List<String> uris = getRealUri(privatePlugin.getPluginInterceptUri());
        if(uris.isEmpty()){
            return false;
        }
        uris.forEach(uri -> {
            if(CommunicateFilter.PrivatePlugins.containsKey(uri)){
                List<PrivatePlugin> _list = CommunicateFilter.PrivatePlugins.get(uri)
                        .stream().filter(plugin -> !id.equals(plugin.getID())).collect(Collectors.toList());
                CommunicateFilter.PrivatePlugins.put(uri, _list);
            }
        });
        return true;
    }
    
    public boolean unloadPlugin(PublicPlugin publicPlugin) {
        String id = publicPlugin.getID();
        List<String> uris = getRealUri(publicPlugin.getPluginInterceptUri());
        if(uris.isEmpty()){
            return false;
        }
        uris.forEach(uri -> {
            if(CommunicateFilter.PublicPlugins.containsKey(uri)){
                List<PublicPlugin> _list = CommunicateFilter.PublicPlugins.get(uri)
                        .stream().filter(plugin -> !id.equals(plugin.getID())).collect(Collectors.toList());
                CommunicateFilter.PublicPlugins.put(uri, _list);
            }
        });
        return true;
    }
    
    private List<String> getRealUri(List<String> uri){
        List<String> rs = new ArrayList();
        if(uri == null || uri.isEmpty())
            return rs;
        uri.forEach(item -> {
            String _uri = item;
            if(_uri.startsWith("/"))
                _uri = _uri.substring(1);
            if(!_uri.contains("/"))
                return;
            String prefix = _uri.substring(0, _uri.indexOf("/"));
            if(!CommunicateFilter.getKcServerApiUriList().contains(prefix))
                return;
            rs.add(_uri);
        });
        return rs;
    }
}
