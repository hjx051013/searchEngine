package com.hjx.search_engine.mapper;

import com.hjx.search_engine.entity.ResultItem;
import com.hjx.search_engine.entity.UrlItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface UrlTbMapper {
    String URLITEM_FILEDS = "id,title,pr";
    String TABLE_NAME = "url_tb";
    String RESULTITEM_FIELDS = "id,title,url,text";

    @Results({
            @Result(property = "urlId",column = "id"),
            @Result(property = "title",column = "title"),
            @Result(property = "pageRank",column = "pr")
    })
    @Select("select "+URLITEM_FILEDS+" from "+TABLE_NAME+ " where id in (${selectUrlIdList})")
    List<UrlItem> selectUrlItemByIds(@Param("selectUrlIdList") String selectUrlIdList);

    @Results({
            @Result(property = "urlId",column = "id"),
            @Result(property = "title",column = "title"),
            @Result(property = "link",column = "url"),
            @Result(property = "content",column = "text")
    })
    @Select("select "+RESULTITEM_FIELDS+" from "+TABLE_NAME+" where id in (${idList}) order by field(id,${idList})")
    List<ResultItem> selectResultItemsByIds(@Param("idList") String subRankedUrlIds);
}