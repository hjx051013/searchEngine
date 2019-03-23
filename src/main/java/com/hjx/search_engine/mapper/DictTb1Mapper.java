package com.hjx.search_engine.mapper;

import com.hjx.search_engine.entity.DictTb1;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DictTb1Mapper {
    String TABLE_NAME = "dict_tb1";
    String SELECT_FILEDS = "id,word,result";
//    int deleteByPrimaryKey(Integer id);
//
//    int insert(DictTb1 record);
//
//    int insertSelective(DictTb1 record);
//
//    DictTb1 selectByPrimaryKey(Integer id);
//
//    int updateByPrimaryKeySelective(DictTb1 record);
//
//    int updateByPrimaryKeyWithBLOBs(DictTb1 record);
//
//    int updateByPrimaryKey(DictTb1 record);
    @Select("select "+SELECT_FILEDS+" from "+TABLE_NAME+" where word=#{word}")
    DictTb1 selectByWord(@Param("word") String word);

    @Select("select "+SELECT_FILEDS+" from "+TABLE_NAME+" where word in (${select_words})")
    List<DictTb1> selectByWords(@Param("select_words") String words);
}