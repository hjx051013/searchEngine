package com.hjx.search_engine.service;

import com.hjx.search_engine.common.StringUtils;
import com.hjx.search_engine.entity.DictTb1;
import com.hjx.search_engine.entity.ResultItem;
import com.hjx.search_engine.entity.UrlItem;
import com.hjx.search_engine.mapper.DictTb1Mapper;
import com.hjx.search_engine.mapper.UrlTbMapper;
import com.huaban.analysis.jieba.JiebaSegmenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(rollbackFor = Exception.class)
public class SearchServiceImpl implements SearchService {

    @Autowired
    private DictTb1Mapper dictTb1Mapper;

    @Autowired
    private UrlTbMapper urlTbMapper;

    @Autowired
    @SuppressWarnings("rawtypes")
    private RedisTemplate redisTemplate;

    private final static int SPAN_SIZE = 70;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<String> getPageQueryWords(List<DictTb1> dictTb1List,Integer id) {
        List<String> pageQueryWords = new ArrayList<>();
        String startPattern = id+",";
        String endPattern = ","+id;
        for(DictTb1 dictTb1:dictTb1List) {
            String result = dictTb1.getResult();
            if(result.indexOf(","+id+",")!=-1||result.startsWith(startPattern)||result.endsWith(endPattern)) pageQueryWords.add(dictTb1.getWord());
            //如果在改词的倒排索引序列中发现对应id,将其加入pageQueryWords
        }
        return pageQueryWords;
    }

    private String getRankedurlIds(List<UrlItem> urlItemList) {
        StringBuilder sb = new StringBuilder();
        for(UrlItem urlItem:urlItemList) {
            sb.append(urlItem.getUrlId()+",");
        }
        return sb.substring(0,sb.length()-1);
    }

    @Override
    public List<UrlItem> getSearchResult(String keyword) {
        List<String> keywords = StringUtils.jiebaSegment(keyword,JiebaSegmenter.SegMode.SEARCH);

        //组装关键词字符串
        String selectWords = "'"+StringUtils.concatWith(keywords,"','")+"'";
        logger.info("节点一："+System.currentTimeMillis());
        List<DictTb1> dictTb1List = dictTb1Mapper.selectByWords(selectWords);
        logger.info("节点二："+System.currentTimeMillis());
        //组装含有关键词的urlId的字符串
        List<String> urlidList = new ArrayList<>();
        for(DictTb1 dictTb1:dictTb1List) {
            urlidList.add(dictTb1.getResult().toString());
        }
        String selectUrlidList = StringUtils.concatWith(urlidList,",");
        UrlItem.setQuery(keywords);
        logger.info("节点三："+System.currentTimeMillis());
        List<UrlItem> urlItemList = urlTbMapper.selectUrlItemByIds(selectUrlidList);
        logger.info("节点四："+System.currentTimeMillis());

        for(UrlItem urlItem:urlItemList) {
            //对于查找结果中的每个urlId
            List<String> pageQueryWords = getPageQueryWords(dictTb1List,urlItem.getUrlId());
            urlItem.setWordList(pageQueryWords);
//            urlItem.setQueryWordNumInTitle();
        }
        logger.info("节点五："+System.currentTimeMillis());

        Collections.sort(urlItemList);
        logger.info("节点六："+System.currentTimeMillis());

        return urlItemList;
    }





    /**
     * 根据urlId字符串来提取相应的ResultItem(包含id,title,link,description)
     * @param subRankedUrlIds
     * @param keyword
     * @return
     */
    private List<ResultItem> extractResultItems(String subRankedUrlIds,String keyword) {
        //采用结巴分词的search模式对请求进行分词，有重复
        List<String> keywordList = StringUtils.jiebaSegment(keyword,JiebaSegmenter.SegMode.SEARCH);
        //向数据库请求subRankedUrlIds对应的ResultItem
        List<ResultItem> resultItemList = urlTbMapper.selectResultItemsByIds(subRankedUrlIds);
        //将resultItemList中的每个ResultItem的content提取出出现请求关键字最集中的文本段放入到discription中
        for(ResultItem resultItem:resultItemList) {
            resultItem.setTitle(StringUtils.highlightStr(resultItem.getTitle(),keywordList));
            resultItem.setDiscription(StringUtils.contentToDiscription(resultItem.getContent(),keywordList,SPAN_SIZE));
            resultItem.setContent(null);//删除正文文本，减少无效数据传输
        }
        return resultItemList;
    }

    /**
     * 根据排序好的网页id字符串获得从第start起共num个的ResultItem,keyword用于从网页正文部分提取关键字聚集文段
     * @param keyword
     * @param rankedUrlIds
     * @param start
     * @param num
     * @return
     */
    private List<ResultItem> getResultItemListByRankedUrlIds(String keyword,String rankedUrlIds,int start,int num) {
        List<ResultItem> resultItemList = new ArrayList<>();
        String subRankedUrlIds = StringUtils.getElementsWith(rankedUrlIds,start,num,",");//获得start到start+num之间的字符串
        if(subRankedUrlIds!=null) {//这样的子串有,则对标题进行高亮处理以及对正文提取关键文本段
            resultItemList = extractResultItems(subRankedUrlIds,keyword);
        }
        return resultItemList;
    }

    @Override
    public Map<String, Object> searchHandle(String keyword, Integer start, Integer num) {
        if(start==null) start = 0;
        if(num==null) num = 10;
        Map<String,Object> mp = new HashMap<>();;
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        List<ResultItem> resultItemList;
        String rankedUrlIds = valueOperations.get(keyword);
        if(rankedUrlIds!=null) {
            //缓存中有该关键词队列,则取出相应位置的元素
            resultItemList = getResultItemListByRankedUrlIds(keyword,rankedUrlIds,start,num);
        }

        else {
            //如果缓存中没有关键词队列，则调用service层函数，并将返回结果存入redis缓存中，并且处理返回给前端浏览器
            List<UrlItem> urlItemList = getSearchResult(keyword);
            rankedUrlIds = getRankedurlIds(urlItemList);
            //将排序好的urlIdList缓存10min
            redisTemplate.opsForValue().set(keyword,rankedUrlIds,600, TimeUnit.SECONDS);
            resultItemList = getResultItemListByRankedUrlIds(keyword,rankedUrlIds,start,num);
        }
        mp.put("data",resultItemList);
        return mp;
    }


}
