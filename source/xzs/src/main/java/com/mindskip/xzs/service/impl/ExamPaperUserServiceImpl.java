package com.mindskip.xzs.service.impl;

import com.mindskip.xzs.domain.ExamPaperUser;
import com.mindskip.xzs.domain.UserSubject;
import com.mindskip.xzs.repository.ExamPaperUserMapper;
import com.mindskip.xzs.repository.UserSubjectMapper;
import com.mindskip.xzs.service.ExamPaperUserService;
import com.mindskip.xzs.service.UserSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamPaperUserServiceImpl extends BaseServiceImpl<ExamPaperUser> implements ExamPaperUserService {

    private final static String CACHE_NAME = "xzs:examPaperUser";
    private final ExamPaperUserMapper examPaperUserMapper;

    @Autowired
    public ExamPaperUserServiceImpl(ExamPaperUserMapper examPaperUserMapper) {
        super(examPaperUserMapper);
        this.examPaperUserMapper = examPaperUserMapper;
    }


    @Override
    public List<ExamPaperUser> findByUserId(Integer userId) {
        return examPaperUserMapper.findByUserId(userId);
    }

    @Override
    public List<ExamPaperUser> findByPaperId(Integer paperId) {
        return examPaperUserMapper.findByPaperId(paperId);
    }

    @Override
    public int deleteByPaperId(Integer paperId) {
        return examPaperUserMapper.deleteByPaperId(paperId);
    }

    @Override
    public void insertBatch(List<ExamPaperUser> list) {
        examPaperUserMapper.insertBatch(list);
    }

    @Override
    public List<ExamPaperUser> findByPaperIds(List<Integer> examPaperIds) {
        return examPaperUserMapper.findByPaperIds(examPaperIds);
    }
}
