/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kcwiki.x.cic.plugin.impl.cicpanel;

import java.util.ArrayList;
import java.util.List;
import kcwiki.x.cic.plugin.PublicPlugin;

/**
 *
 * @author x5171
 */
public class Callback extends PublicPlugin {
    Controller controller;

    @Override
    public void callback(String uri, String content, boolean isResponse) {
        controller.handleCallback(uri, content, isResponse);
    }

    @Override
    public String getPluginName() {
        return "";
    }

    @Override
    public String getPluginVersion() {
        return "";
    }

    @Override
    public String getPluginDescribe() {
        return "";
    }

    @Override
    public String getPluginAuthor() {
        return "";
    }

    @Override
    public List<String> getPluginInterceptUri() {
        List<String> rs = new ArrayList();
        rs.add("/kcsapi/api_req_member/get_incentive");
        rs.add("kcsapi/api_get_member/require_info");
        rs.add("/kcsapi/api_port/port");
        return rs;
    }

    @Override
    public void init() {
        if(controller == null)
            controller = new Controller();
        controller.init();
    }
    
}
