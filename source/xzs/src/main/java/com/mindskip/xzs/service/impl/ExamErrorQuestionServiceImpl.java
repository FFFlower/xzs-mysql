package com.mindskip.xzs.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mindskip.xzs.domain.ExamErrorQuestion;
import com.mindskip.xzs.domain.ExamPaperQuestionCustomerAnswer;
import com.mindskip.xzs.domain.TextContent;
import com.mindskip.xzs.domain.enums.QuestionTypeEnum;
import com.mindskip.xzs.domain.other.ExamPaperAnswerUpdate;
import com.mindskip.xzs.domain.other.KeyValue;
import com.mindskip.xzs.repository.ExamErrorQuestionMapper;
import com.mindskip.xzs.repository.ExamPaperQuestionCustomerAnswerMapper;
import com.mindskip.xzs.service.ExamErrorQuestionService;
import com.mindskip.xzs.service.ExamPaperQuestionCustomerAnswerService;
import com.mindskip.xzs.service.TextContentService;
import com.mindskip.xzs.utility.DateTimeUtil;
import com.mindskip.xzs.utility.ExamUtil;
import com.mindskip.xzs.utility.JsonUtil;
import com.mindskip.xzs.viewmodel.student.exam.ExamPaperSubmitItemVM;
import com.mindskip.xzs.viewmodel.student.question.answer.QuestionPageStudentRequestVM;
import com.mindskip.xzs.viewmodel.student.question.answer.QuestionStudentRequestVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamErrorQuestionServiceImpl extends BaseServiceImpl<ExamErrorQuestion> implements ExamErrorQuestionService {

    private final ExamErrorQuestionMapper examErrorQuestionMapper;
    private final TextContentService textContentService;

    @Autowired
    public ExamErrorQuestionServiceImpl(ExamErrorQuestionMapper examErrorQuestionMapper, TextContentService textContentService) {
        super(examErrorQuestionMapper);
        this.examErrorQuestionMapper = examErrorQuestionMapper;
        this.textContentService = textContentService;
    }

    @Override
    public PageInfo<ExamErrorQuestion> studentPage(QuestionPageStudentRequestVM requestVM) {
        return PageHelper.startPage(requestVM.getPageIndex(), requestVM.getPageSize(), "id desc").doSelectPageInfo(() ->
                examErrorQuestionMapper.studentPage(requestVM)
        );
    }

    @Override
    public List<ExamErrorQuestion> selectListByPaperAnswerId(Integer id) {
        return examErrorQuestionMapper.selectListByPaperAnswerId(id);
    }

    @Override
    public void insertList(List<ExamErrorQuestion> examErrorQuestions) {
        examErrorQuestionMapper.insertList(examErrorQuestions);
    }

    @Override
    public Integer selectAllCount() {
        return examErrorQuestionMapper.selectAllCount();
    }

    @Override
    public Integer selectUserCount(Integer userId) {
        QuestionStudentRequestVM requestVM = new QuestionStudentRequestVM();
        requestVM.setCreateUser(userId);
        return examErrorQuestionMapper.selectCount(requestVM);
    }

    @Override
    public void deleteByPrimaryKey(Integer id) {
        examErrorQuestionMapper.deleteByPrimaryKey(id);
    }

}
