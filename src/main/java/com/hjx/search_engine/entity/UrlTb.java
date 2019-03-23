package com.hjx.search_engine.entity;

public class UrlTb {
    private Integer id;

    private String url;

    private Float pr;

    private String title;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Float getPr() {
        return pr;
    }

    public void setPr(Float pr) {
        this.pr = pr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }
}