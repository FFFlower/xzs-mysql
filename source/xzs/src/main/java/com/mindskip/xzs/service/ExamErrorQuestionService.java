package com.mindskip.xzs.service;

import com.github.pagehelper.PageInfo;
import com.mindskip.xzs.domain.ExamErrorQuestion;
import com.mindskip.xzs.domain.ExamPaperQuestionCustomerAnswer;
import com.mindskip.xzs.domain.other.ExamPaperAnswerUpdate;
import com.mindskip.xzs.viewmodel.student.exam.ExamPaperSubmitItemVM;
import com.mindskip.xzs.viewmodel.student.question.answer.QuestionPageStudentRequestVM;

import java.util.List;

public interface ExamErrorQuestionService extends BaseService<ExamErrorQuestion> {

    PageInfo<ExamErrorQuestion> studentPage(QuestionPageStudentRequestVM requestVM);

    List<ExamErrorQuestion> selectListByPaperAnswerId(Integer id);

    void insertList(List<ExamErrorQuestion> examErrorQuestions);

    Integer selectAllCount();

    Integer selectUserCount(Integer userId);

    void deleteByPrimaryKey(Integer id);
}
