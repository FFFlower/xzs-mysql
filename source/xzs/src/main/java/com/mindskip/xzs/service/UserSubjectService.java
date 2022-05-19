package com.mindskip.xzs.service;

import com.mindskip.xzs.domain.UserSubject;

import java.util.List;

public interface UserSubjectService extends BaseService<UserSubject> {

    List<UserSubject> findByUserId(Integer userId);

    int deleteByUserId(Integer userId);

    void insertBatch(List<UserSubject> subjects);

}
