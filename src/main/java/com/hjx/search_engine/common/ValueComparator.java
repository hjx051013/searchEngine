package com.hjx.search_engine.common;

import com.hjx.search_engine.entity.UrlItem;

import java.util.Comparator;
import java.util.Map;

public class ValueComparator implements Comparator<Map.Entry<String,UrlItem>> {

    @Override
    @SuppressWarnings("all")
    public int compare(Map.Entry mp1, Map.Entry mp2) {
        UrlItem item1 = (UrlItem) mp1.getValue();
        UrlItem item2 = (UrlItem) mp2.getValue();
        int item1_size,item2_size;
        double pr1,pr2;
        if((item1_size = item1.getWordList().size())!=(item2_size = item2.getWordList().size())) {
            if(item1_size < item2_size) return 1;
            else if(item1_size==item2_size) return 0;
            else return -1;
        }
        pr1 = item1.getPageRank();
        pr2 = item2.getPageRank();
        if(pr1 < pr2) return 1;
        else if(pr1==pr2) return 0;
        else return -1;
    }
}