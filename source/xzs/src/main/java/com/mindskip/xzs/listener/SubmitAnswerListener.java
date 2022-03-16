package com.mindskip.xzs.listener;

import com.mindskip.xzs.domain.ExamErrorQuestion;
import com.mindskip.xzs.domain.Question;
import com.mindskip.xzs.event.SubmitAnswerEvent;
import com.mindskip.xzs.service.ExamErrorQuestionService;
import com.mindskip.xzs.service.QuestionService;
import com.mindskip.xzs.viewmodel.student.question.answer.QuestionAnswerRecordRequestVM;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p><一句话功能简述>
 * <p><功能详细描述>
 * <p>Copyright (c) 2021/12/25, wangyu@zhuofansoft.com All Rights Reserve</p>
 *
 * @author wangyu
 * @version [V1.0, 2021/12/25]
 * @see [相关类/方法]
 */
@Component
public class SubmitAnswerListener implements ApplicationListener<SubmitAnswerEvent> {

    private final ExamErrorQuestionService examErrorQuestionService;
    private final QuestionService questionService;

    public SubmitAnswerListener(ExamErrorQuestionService examErrorQuestionService, QuestionService questionService) {
        this.examErrorQuestionService = examErrorQuestionService;
        this.questionService = questionService;
    }

    @Override
    public void onApplicationEvent(SubmitAnswerEvent submitAnswerEvent) {
        QuestionAnswerRecordRequestVM requestVM = submitAnswerEvent.getRequestVM();
        Question question = questionService.get(requestVM.getQuestionId());
        if (null == question) {
            return;
        }
        if (!question.getCorrect().equals(requestVM.getAnswer())) {
            ExamErrorQuestion errorQuestion = new ExamErrorQuestion();
            errorQuestion.setAnswer(requestVM.getAnswer());
            errorQuestion.setQuestionType(question.getQuestionType());
            errorQuestion.setCreateTime(new Date());
            errorQuestion.setQuestionId(question.getId());
            errorQuestion.setSubjectId(question.getSubjectId());
            errorQuestion.setCreateUser(requestVM.getCreateUser());
            errorQuestion.setQuestionTextContentId(question.getInfoTextContentId());
            List<ExamErrorQuestion> list = new ArrayList<>();
            list.add(errorQuestion);
            examErrorQuestionService.insertList(list);
        }
    }
}
