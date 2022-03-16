package com.mindskip.xzs.repository;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseMapper<T> {

    int deleteByPrimaryKey(Integer id);

    int insert(T record);

    int insertSelective(T record);

    T selectByPrimaryKey(Integer id);

    List<T> selectByPrimaryKeys(List<Integer> ids);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);
}
