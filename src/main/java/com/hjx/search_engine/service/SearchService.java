package com.hjx.search_engine.service;

import com.hjx.search_engine.entity.ResultItem;
import com.hjx.search_engine.entity.UrlItem;

import java.util.List;
import java.util.Map;

public interface SearchService {

    public List<UrlItem> getSearchResult(String keyword);

    public Map<String,Object> searchHandle(String keyword, Integer start, Integer num);
}
