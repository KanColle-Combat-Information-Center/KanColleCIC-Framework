/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kcwiki.x.cic.plugin;

import java.util.List;

/**
 *
 * @author x5171
 */
public abstract class BasePlugin {
    
    private String ID;
    
    private long errorNum = 0L;
    
    public abstract String getPluginName();

    public abstract String getPluginVersion();

    public abstract String getPluginDescribe();
    
    public abstract String getPluginAuthor();
    
    public abstract List<String> getPluginInterceptUri();

    /**
     * @return the ID
     */
    public String getID() {
        return ID;
    }

    /**
     * @param ID the ID to set
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * @return the errorNum
     */
    public long getErrorNum() {
        return errorNum;
    }

    /**
     * @param errorNum the errorNum to set
     */
    public void setErrorNum(long errorNum) {
        this.errorNum = errorNum;
    }
    
    
    
}
