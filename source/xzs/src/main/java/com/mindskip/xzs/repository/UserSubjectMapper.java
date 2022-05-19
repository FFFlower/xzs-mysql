package com.mindskip.xzs.repository;

import com.mindskip.xzs.domain.Subject;
import com.mindskip.xzs.domain.UserSubject;
import com.mindskip.xzs.viewmodel.admin.education.SubjectPageRequestVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserSubjectMapper extends BaseMapper<UserSubject> {
    int deleteByPrimaryKey(Integer id);

    int insert(UserSubject record);

    int insertSelective(UserSubject record);

    UserSubject selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserSubject record);

    int updateByPrimaryKey(UserSubject record);

    List<UserSubject> findByCondition(UserSubject record);

    List<UserSubject> findByUserId(Integer userId);

    int deleteByUserId(Integer userId);

    void insertBatch(List<UserSubject> list);
}
