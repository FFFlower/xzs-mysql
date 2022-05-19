package com.mindskip.xzs.repository;

import com.mindskip.xzs.domain.ExamPaperUser;
import com.mindskip.xzs.domain.ExamPaperUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExamPaperUserMapper extends BaseMapper<ExamPaperUser> {
    int deleteByPrimaryKey(Integer id);

    int insert(ExamPaperUser record);

    int insertSelective(ExamPaperUser record);

    ExamPaperUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ExamPaperUser record);

    int updateByPrimaryKey(ExamPaperUser record);

    List<ExamPaperUser> findByCondition(ExamPaperUser record);

    List<ExamPaperUser> findByUserId(Integer userId);

    List<ExamPaperUser> findByPaperId(Integer paperId);

    int deleteByPaperId(Integer paperId);

    void insertBatch(List<ExamPaperUser> list);

    List<ExamPaperUser> findByPaperIds(List<Integer> examPaperIds);
}
