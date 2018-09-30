/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kcwiki.x.cic.plugin.impl.cicpanel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileWriter;
import java.io.IOException;
import kcwiki.x.cic.plugin.impl.cicpanel.entity.port.Port;
import kcwiki.x.cic.plugin.impl.cicpanel.gui.CicPanel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author x5171
 */
public class Controller extends Callback {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Controller.class);
    CicPanel cicPanel = new CicPanel();
    boolean isShow = false;
    
    FileWriter fw = null;
    
    public void handleCallback(String uri, String content, boolean isResponse) {
        if(uri.contains("api_port/port")){
            if(!isResponse)
                return;
            Port port;
            try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    port = objectMapper.readValue(content,
                        new TypeReference<Port>(){});
            } catch (IOException ex) {
                ExceptionUtils.getStackTrace(ex);
                return;
            }
            kcwiki.x.cic.plugin.impl.cicpanel.entity.port.Api_data api_data = port.getApi_data();
//            cicPanel = new CicPanel();
            cicPanel.updatePortData(api_data);
            if(!isShow) {
                cicPanel.createPanel();
                isShow = true;
            }
//            cicPanel.destroyPanel();
//            isShow = false;
        } else if (uri.contains("api_get_member/require_info")){
//            Require_info require_info;
//            try {
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    require_info = objectMapper.readValue(content,
//                        new TypeReference<Require_info>(){});
//            } catch (IOException ex) {
//                return;
//            }
//            kcwiki.x.cic.plugin.impl.cicpanel.entity.require_info.Api_data api_data = require_info.getApi_data();
//            
            
        }
    }
    
    @Override
    public void init(){
        
    }
    
    private String object2str(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (IOException ex) {
            
        }
        return json;
    }
    
}
