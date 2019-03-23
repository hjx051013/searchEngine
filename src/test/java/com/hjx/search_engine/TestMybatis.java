package com.hjx.search_engine;

import com.hjx.search_engine.entity.DictTb1;
import com.hjx.search_engine.entity.ResultItem;
import com.hjx.search_engine.mapper.DictTb1Mapper;
import com.hjx.search_engine.mapper.UrlTbMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TestMybatis {
    @Autowired
    private DictTb1Mapper dictTb1Mapper;

    @Autowired
    private UrlTbMapper urlTbMapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testDictTb1() {
        String word = "浙江大学";
        DictTb1 dictTb1 = dictTb1Mapper.selectByWord(word);
        System.out.println(dictTb1.getId() + "," + dictTb1.getWord() + "," + dictTb1.getResult());
    }

    @Test
    public void testDictTb1Mapper() {
        String selectWords = "'浙江大学','软件','学院'";
        List<DictTb1> resList = dictTb1Mapper.selectByWords(selectWords);
        logger.info("获取元素列表大小" + resList.size());
        for (DictTb1 res : resList) {
            logger.info(res.toString());
        }
    }

    @Test
    public void testUrlTbMapper() {
        String subRankedUrlIds = "76,110,61,104,4,41,91,46,86";
        List<ResultItem> resultItemList = urlTbMapper.selectResultItemsByIds(subRankedUrlIds);
        for (ResultItem resultItem : resultItemList) {
            logger.info(resultItem.toString());
        }
    }
    //    @Test
//    public void testGetPageQueryWords() {
//        String selectWords = "'浙江大学','管理系统','学院'";
//        List<DictTb1> resList = dictTb1Mapper.selectByWords(selectWords);
//        List<String> queryWords = getPageQueryWords(resList,10);
//        System.out.println(queryWords);
//    }
//
//    private List<String> getPageQueryWords(List<DictTb1> dictTb1List, Integer id) {
//        List<String> pageQueryWords = new ArrayList<>();
//        for(DictTb1 dictTb1:dictTb1List) {
//            String result = dictTb1.getResult();
//            Pattern pattern = Pattern.compile("(^|,)"+id+"(,|$)");
//            Matcher mat = pattern.matcher(result);
//            if(mat.find()) pageQueryWords.add(dictTb1.getWord());//如果在改词的倒排索引序列中发现对应id,将其加入pageQueryWords
//        }
//        return pageQueryWords;
//    }
}
