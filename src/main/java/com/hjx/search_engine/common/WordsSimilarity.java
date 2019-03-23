package com.hjx.search_engine.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WordsSimilarity {

    private static int getMultiple(Set<Map.Entry<String,Pair>> set) {
        int res = 0;
        for(Map.Entry entry:set) {
            Pair<Integer> pair = (Pair)entry.getValue();
            res += pair.first*pair.second;
        }
        return res;

    }

    private static int getPairAbs(Set<Map.Entry<String,Pair>> set, boolean isFirst) {
        int res = 0;
        for(Map.Entry entry:set) {
            Pair<Integer> pair = (Pair<Integer>)entry.getValue();
            if(isFirst) res += pair.first*pair.first;
            else res += pair.second*pair.second;
        }
        return res;
    }

    public static double getSimilarity(List<String> list1,List<String> list2) {
        Map<String,Pair> mp = new HashMap<>();
        Pair<Integer> val;
        for(String word:list1) {
            if((val=mp.get(word)) == null) {
                mp.put(word,new Pair(1,0));
            }
            else {
                val.first = 1;
            }
        }
        for(String word:list1) {
            if((val=mp.get(word)) == null) {
                mp.put(word,new Pair(0,1));
            }
            else {
                val.second = 1;
            }
        }
        int multiple = getMultiple(mp.entrySet());
        int list1Abs = getPairAbs(mp.entrySet(),true);
        int list2Abs = getPairAbs(mp.entrySet(),false);

        return (double)multiple/list1Abs*list2Abs;
    }
}
