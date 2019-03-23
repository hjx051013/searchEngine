package com.hjx.search_engine;

import com.hjx.search_engine.entity.ResultItem;
import com.hjx.search_engine.entity.UrlItem;
import com.hjx.search_engine.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestService {

//    @Test
//    public void testComparator() {
//        Map<String, UrlItem> map = new HashMap<>();
//        List<String> list1 = new ArrayList<>();
//        Collections.addAll(list1,"hello","world","this");
//        List<String> list2 = new ArrayList<>();
//        Collections.addAll(list2,"very","good");
//        List<String> list3 = new ArrayList<>();
//        Collections.addAll(list3,"hh","gg","ss");
//
//        UrlItem item1 = new UrlItem("1",0.13,list1);
//        UrlItem item2 = new UrlItem("2",0.13,list2);
//        UrlItem item3 = new UrlItem("3",0.14,list3);
//
//        map.put("1",item1);
//        map.put("2",item2);
//        map.put("3",item3);
//        List<Map.Entry> list = new ArrayList<>();
//        list.addAll(map.entrySet());
//        Collections.sort(list,new ValueComparator());
//        System.out.println(list);
//    }


    @Autowired
    private SearchService searchService;

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Test
    public void testString() {
        StringBuilder sb = new StringBuilder("hello,world,ok,nice,");
        sb.deleteCharAt(sb.length()-1);
        System.out.println(sb.toString());
    }

    @Test
    public void testGetSearchResult() {
        String query = "浙江大学软件学院";
        logger.info("开始："+ System.currentTimeMillis());
        List<UrlItem> urlItemList = searchService.getSearchResult(query);
        long time2 = System.currentTimeMillis();
        logger.info("结束："+ System.currentTimeMillis());
        int count = 0;
        for(UrlItem urlItem:urlItemList) {
            if(count>10) break;
            logger.info(urlItem.toString());
            count++;
        }
    }

    @Test
    public void testSearchHandle() {
        String query = "软件学院拟录取名单";
        Map<String,Object> mp = searchService.searchHandle(query,0,10);
        logger.info(((List<ResultItem>)mp.get("data")).toString());

    }
}
