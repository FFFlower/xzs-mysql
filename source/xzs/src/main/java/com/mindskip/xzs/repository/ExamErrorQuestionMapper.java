package com.mindskip.xzs.repository;

import com.mindskip.xzs.domain.ExamErrorQuestion;
import com.mindskip.xzs.domain.other.KeyValue;
import com.mindskip.xzs.viewmodel.student.question.answer.QuestionPageStudentRequestVM;
import com.mindskip.xzs.viewmodel.student.question.answer.QuestionStudentRequestVM;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface ExamErrorQuestionMapper extends BaseMapper<ExamErrorQuestion> {
    int deleteByPrimaryKey(Integer id);

    int insert(ExamErrorQuestion record);

    int insertSelective(ExamErrorQuestion record);

    ExamErrorQuestion selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(ExamErrorQuestion record);

    List<ExamErrorQuestion> selectListByPaperAnswerId(Integer id);

    List<ExamErrorQuestion> studentPage(QuestionPageStudentRequestVM requestVM);

    int insertList(List<ExamErrorQuestion> list);

    Integer selectAllCount();

    Integer selectCount(QuestionStudentRequestVM requestVM);

    List<KeyValue> selectCountByDate(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

}
