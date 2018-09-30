/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kcwiki.x.cic.cache.inmem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import kcwiki.x.cic.plugin.PrivatePlugin;
import kcwiki.x.cic.plugin.PublicPlugin;

/**
 *
 * @author x5171
 */
public class CommunicateFilter {
    
    private static final List<String> KcServerHostList = new ArrayList();
    private static final List<String> KcServerApiUriList = new ArrayList();
    
    public static final Map<String, List<PublicPlugin>> PublicPlugins = new ConcurrentHashMap();
    public static final Map<String, List<PrivatePlugin>> PrivatePlugins = new ConcurrentHashMap();
    
    
    static{
        KcServerHostList.add("203.104.209.102");
        KcServerHostList.add("125.6.189.39");
        
        KcServerApiUriList.add("kcsapi");
                
    }

    /**
     * @return the KcServerHostList
     */
    public static List<String> getKcServerHostList() {
        List<String> _list = new ArrayList();
        _list.addAll(KcServerHostList);
        return _list;
    }

    /**
     * @return the KcServerApiUriList
     */
    public static List<String> getKcServerApiUriList() {
        List<String> _list = new ArrayList();
        _list.addAll(KcServerApiUriList);
        return _list;
    }
}
