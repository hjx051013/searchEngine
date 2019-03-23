package com.hjx.search_engine.entity;

import org.springframework.data.annotation.Transient;

public class ResultItem {
    private Integer urlId;
    private String title;
    private String link;
    private String content;
    @Transient
    private String discription;

    @Override
    public String toString() {
        return "ResultItem{" +
                "urlId=" + urlId +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", content='" + content + '\'' +
                ", discription='" + discription + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public Integer getUrlId() {
        return urlId;
    }

    public void setUrlId(Integer urlId) {
        this.urlId = urlId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}