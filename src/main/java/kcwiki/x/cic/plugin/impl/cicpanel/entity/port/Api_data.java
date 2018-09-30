/**
  * Copyright 2018 bejson.com 
  */
package kcwiki.x.cic.plugin.impl.cicpanel.entity.port;
import java.util.List;

/**
 * Auto-generated: 2018-09-28 17:12:40
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Api_data {

    private List<Api_material> api_material;
    private List<Api_deck_port> api_deck_port;
    private List<Api_ndock> api_ndock;
    private List<Api_ship> api_ship;
    private Api_basic api_basic;
    private List<Api_log> api_log;
    private int api_combined_flag;
    private int api_p_bgm_id;
    private int api_parallel_quest_count;
    private int api_dest_ship_slot;
    private Api_event_object api_event_object;
    public void setApi_material(List<Api_material> api_material) {
         this.api_material = api_material;
     }
     public List<Api_material> getApi_material() {
         return api_material;
     }

    public void setApi_deck_port(List<Api_deck_port> api_deck_port) {
         this.api_deck_port = api_deck_port;
     }
     public List<Api_deck_port> getApi_deck_port() {
         return api_deck_port;
     }

    public void setApi_ndock(List<Api_ndock> api_ndock) {
         this.api_ndock = api_ndock;
     }
     public List<Api_ndock> getApi_ndock() {
         return api_ndock;
     }

    public void setApi_ship(List<Api_ship> api_ship) {
         this.api_ship = api_ship;
     }
     public List<Api_ship> getApi_ship() {
         return api_ship;
     }

    public void setApi_basic(Api_basic api_basic) {
         this.api_basic = api_basic;
     }
     public Api_basic getApi_basic() {
         return api_basic;
     }

    public void setApi_log(List<Api_log> api_log) {
         this.api_log = api_log;
     }
     public List<Api_log> getApi_log() {
         return api_log;
     }

    public void setApi_combined_flag(int api_combined_flag) {
         this.api_combined_flag = api_combined_flag;
     }
     public int getApi_combined_flag() {
         return api_combined_flag;
     }

    public void setApi_p_bgm_id(int api_p_bgm_id) {
         this.api_p_bgm_id = api_p_bgm_id;
     }
     public int getApi_p_bgm_id() {
         return api_p_bgm_id;
     }

    public void setApi_parallel_quest_count(int api_parallel_quest_count) {
         this.api_parallel_quest_count = api_parallel_quest_count;
     }
     public int getApi_parallel_quest_count() {
         return api_parallel_quest_count;
     }

    public void setApi_dest_ship_slot(int api_dest_ship_slot) {
         this.api_dest_ship_slot = api_dest_ship_slot;
     }
     public int getApi_dest_ship_slot() {
         return api_dest_ship_slot;
     }

    public void setApi_event_object(Api_event_object api_event_object) {
         this.api_event_object = api_event_object;
     }
     public Api_event_object getApi_event_object() {
         return api_event_object;
     }

}