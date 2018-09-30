/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kcwiki.x.cic.cache.inmem;

import java.util.HashMap;
import java.util.Map;
import kcwiki.x.cic.initializer.AppInitializer;

/**
 *
 * @author x5171
 */
public class SystemData {

    /**
     * @return the RankList
     */
    public static Map<Integer, String> getRankList() {
        return RankList;
    }
    private static boolean isReadyIntercept = false;
    
    private static final Map<Integer, String> RankList = new HashMap();
    
    static{
        getRankList().put(1, "元帥");
        getRankList().put(2, "大将");
        getRankList().put(3, "中将");
        getRankList().put(4, "少将");
        getRankList().put(5, "大佐");
        getRankList().put(6, "中佐");
        getRankList().put(7, "新米中佐");
        getRankList().put(8, "少佐");
        getRankList().put(9, "中堅少佐");
        getRankList().put(10, "新米少佐");
    }

    /**
     * @return the isReadyIntercept
     */
    public static boolean isIsReadyIntercept() {
        return isReadyIntercept;
    }

    /**
     * @param invoker
     * @param aIsReadyIntercept the isReadyIntercept to set
     */
    public static void setIsReadyIntercept(Object invoker, boolean aIsReadyIntercept) {
        if(!(invoker instanceof AppInitializer))
            return;
        isReadyIntercept = aIsReadyIntercept;
    }
}
