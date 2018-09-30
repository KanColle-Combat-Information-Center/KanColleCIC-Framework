/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kcwiki.x.cic.test.proxy;

import kcwiki.x.cic.initializer.AppInitializer;
import org.junit.Test;

/**
 *
 * @author x5171
 */
public class proxytestcase {
    
    @Test
    public void test() {
        AppInitializer appInitializer = AppInitializer.getInstance();
        appInitializer.startup();
    }
    
}
