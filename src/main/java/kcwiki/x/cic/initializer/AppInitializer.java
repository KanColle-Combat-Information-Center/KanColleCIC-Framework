/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kcwiki.x.cic.initializer;

import kcwiki.x.cic.cache.inmem.SystemData;
import kcwiki.x.cic.plugin.manage.PluginManager;
import kcwiki.x.cic.proxy.KanColleHttpProxy;

/**
 *
 * @author x5171
 */
public class AppInitializer {
    
    private static AppInitializer appInitializer;
    
    private AppInitializer(){};
    
    public static void main(String[] args) {
        
    }
    
    public static AppInitializer getInstance() {
        if(appInitializer==null){
            appInitializer = new AppInitializer();
        }
        return appInitializer;
    }
    
    public void startup() {
        try{
            PluginManager pluginManager = new PluginManager();
            pluginManager.loadPlugin(new kcwiki.x.cic.plugin.impl.cicpanel.Callback());
        } catch (Exception ex) {
            System.out.print(true);
        }
        SystemData.setIsReadyIntercept(this, true);
        try{
                        MainController mainController = new MainController();
                        mainController.startupProxy(1823);
                    } catch (Exception ex) {
                        System.out.print(true);
        }
    }
    
}
