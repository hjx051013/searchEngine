package com.hjx.search_engine.entity;

import org.springframework.data.annotation.Transient;

import java.util.List;

public class UrlItem implements Comparable{
    private Integer urlId;// url序号
    private float pageRank;//url对应的pagerank
    private String title;//标题的分词
    @Transient
    private int queryWordNumInTitle;
    @Transient
    private List<String> wordList;//正文中存在请求的关键字个数

    private static List<String> query;



    public static void setQuery(List<String> query) {
        if(query==null) throw new IllegalArgumentException("query cannot be null");
        UrlItem.query = query;
    }

    public UrlItem() throws Exception {
        if(query==null) throw new Exception("Uninitialize static parameter query");
    }


    public Integer getUrlId() {
        return urlId;
    }

    public void setUrlId(Integer urlId) {
        this.urlId = urlId;
    }

    public float getPageRank() {
        return pageRank;
    }

    public void setPageRank(float pageRank) {
        this.pageRank = pageRank;
    }

    public List<String> getWordList() {
        return wordList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        for(String word:query) {
            if(title.indexOf(word)!=-1) this.queryWordNumInTitle++;
        }
    }

    public void setWordList(List<String> wordList) {
        this.wordList = wordList;
    }

    @Override
    public String toString() {
        return "UrlItem{" +
                "urlId='" + urlId + '\'' +
                ", pageRank=" + pageRank +
                ", title='" + title + '\'' +
                ", queryWordNumInTitle=" + queryWordNumInTitle +
                ", word_list=" + wordList +
                '}';
    }



    @Override
    public int compareTo(Object other) {
        UrlItem another = (UrlItem)other;
        if(this.queryWordNumInTitle!=another.queryWordNumInTitle) {
            if(this.queryWordNumInTitle < another.queryWordNumInTitle) return 1;
            else return -1;
        }
        int item1_size = this.wordList.size(),item2_size = another.wordList.size();
        if(item1_size!=item2_size) {
            if(item1_size < item2_size) return 1;
            else return -1;
        }
        double pr1 = this.getPageRank(),pr2 = another.getPageRank();
        if(Math.abs(pr1-pr2)< 1E-9) return 0;
        else if(pr1 < pr2) return 1;
        else return -1;
    }


}
