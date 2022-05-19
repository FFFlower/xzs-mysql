package com.mindskip.xzs.service.impl;

import com.mindskip.xzs.domain.UserSubject;
import com.mindskip.xzs.repository.UserSubjectMapper;
import com.mindskip.xzs.service.UserSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserSubjectServiceImpl extends BaseServiceImpl<UserSubject> implements UserSubjectService {

    private final static String CACHE_NAME = "xzs:userSubject";
    private final UserSubjectMapper userSubjectMapper;

    @Autowired
    public UserSubjectServiceImpl(UserSubjectMapper userSubjectMapper) {
        super(userSubjectMapper);
        this.userSubjectMapper = userSubjectMapper;
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#id", unless = "#result == null")
    public UserSubject selectById(Integer id) {
        return super.selectById(id);
    }

    @Override
    @CacheEvict(value = CACHE_NAME, key = "#record.id")
    public int updateByIdFilter(UserSubject record) {
        return super.updateByIdFilter(record);
    }


    @Override
    @Cacheable(value = CACHE_NAME, key = "#userId", unless = "#result == null")
    public List<UserSubject> findByUserId(Integer userId) {
        return userSubjectMapper.findByUserId(userId);
    }

    @Override
    @CacheEvict(value = CACHE_NAME, key = "#userId")
    public int deleteByUserId(Integer userId) {
        return userSubjectMapper.deleteByUserId(userId);
    }

    @Override
    public void insertBatch(List<UserSubject> subjects) {
        userSubjectMapper.insertBatch(subjects);
    }
}
