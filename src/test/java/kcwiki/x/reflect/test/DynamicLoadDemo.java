package kcwiki.x.reflect.test;


import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import kcwiki.x.cic.plugin.BasePlugin;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author x5171
 * 
 */
public class DynamicLoadDemo {
    
    public static void main(String[] args) throws NoSuchMethodException {
//        loadJar();
    }
    
    @Test
    public void loadJar() throws NoSuchMethodException{
        // 系统类库路径
        File libPath = new File("L:\\NetBeans\\NetBeansProjects\\netty-proxy\\build\\libs");

        // 获取所有的.jar和.zip文件
        File[] jarFiles = libPath.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar") || name.endsWith(".zip");
            }
        });

        if (jarFiles != null) {
            try {
                for (File file : jarFiles) {
                    URL url = file.toURI().toURL();
                    Class<?> cls = null;
                    URLClassLoader classLoader = new URLClassLoader((new URL[] {url}));
                    try {
                        cls = classLoader.loadClass("kcwiki.x.cic.proxy.plugin.impl.test.plugintest");
                        BasePlugin basePlugin = (BasePlugin) cls.newInstance();
                        basePlugin.getPluginAuthor();
                        basePlugin.getPluginInterceptUri();
                        System.out.println(String.format("读取jar文件[name=%s]", file.getName()));
                    } catch (Exception e) {
                        System.out.println(String.format("读取jar文件[name=%s]失败", file.getName()));
                    }
                }
            } catch (MalformedURLException ex) {
                Logger.getLogger(DynamicLoadDemo.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                
            }
            
        }
    }
    
}
