package com.mindskip.xzs.controller.student;

import com.mindskip.xzs.base.BaseApiController;
import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.domain.ExamPaperQuestionCustomerAnswer;
import com.mindskip.xzs.domain.Subject;
import com.mindskip.xzs.domain.TextContent;
import com.mindskip.xzs.domain.question.QuestionObject;
import com.mindskip.xzs.event.SubmitAnswerEvent;
import com.mindskip.xzs.service.ExamPaperQuestionCustomerAnswerService;
import com.mindskip.xzs.service.QuestionService;
import com.mindskip.xzs.service.SubjectService;
import com.mindskip.xzs.service.TextContentService;
import com.mindskip.xzs.utility.*;
import com.mindskip.xzs.viewmodel.admin.question.QuestionEditRequestVM;
import com.mindskip.xzs.viewmodel.student.exam.ExamPaperSubmitItemVM;
import com.mindskip.xzs.viewmodel.student.question.answer.QuestionAnswerRecordRequestVM;
import com.mindskip.xzs.viewmodel.student.question.answer.QuestionAnswerVM;
import com.mindskip.xzs.viewmodel.student.question.answer.QuestionPageStudentRequestVM;
import com.mindskip.xzs.viewmodel.student.question.answer.QuestionPageStudentResponseVM;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@RestController("StudentQuestionAnswerController")
@RequiredArgsConstructor
@RequestMapping(value = "/api/student/question/answer")
public class QuestionAnswerController extends BaseApiController {

    private final ExamPaperQuestionCustomerAnswerService examPaperQuestionCustomerAnswerService;
    private final QuestionService questionService;
    private final TextContentService textContentService;
    private final SubjectService subjectService;
    private final RedisUtils redisUtils;
    private final ApplicationEventPublisher eventPublisher;

    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public RestResponse<PageInfo<QuestionPageStudentResponseVM>> pageList(@RequestBody QuestionPageStudentRequestVM model) {
        model.setCreateUser(getCurrentUser().getId());
        PageInfo<ExamPaperQuestionCustomerAnswer> pageInfo = examPaperQuestionCustomerAnswerService.studentPage(model);
        PageInfo<QuestionPageStudentResponseVM> page = PageInfoHelper.copyMap(pageInfo, q -> {
            Subject subject = subjectService.selectById(q.getSubjectId());
            QuestionPageStudentResponseVM vm = modelMapper.map(q, QuestionPageStudentResponseVM.class);
            vm.setCreateTime(DateTimeUtil.dateFormat(q.getCreateTime()));
            TextContent textContent = textContentService.selectById(q.getQuestionTextContentId());
            QuestionObject questionObject = JsonUtil.toJsonObject(textContent.getContent(), QuestionObject.class);
            String clearHtml = HtmlUtil.clear(questionObject.getTitleContent());
            vm.setShortTitle(clearHtml);
            vm.setSubjectName(subject.getName());
            return vm;
        });
        return RestResponse.ok(page);
    }


    @RequestMapping(value = "/select/{id}", method = RequestMethod.POST)
    public RestResponse<QuestionAnswerVM> select(@PathVariable Integer id) {
        QuestionAnswerVM vm = new QuestionAnswerVM();
        ExamPaperQuestionCustomerAnswer examPaperQuestionCustomerAnswer = examPaperQuestionCustomerAnswerService.selectById(id);
        ExamPaperSubmitItemVM questionAnswerVM = examPaperQuestionCustomerAnswerService.examPaperQuestionCustomerAnswerToVM(examPaperQuestionCustomerAnswer);
        QuestionEditRequestVM questionVM = questionService.getQuestionEditRequestVM(examPaperQuestionCustomerAnswer.getQuestionId());
        vm.setQuestionVM(questionVM);
        vm.setQuestionAnswerVM(questionAnswerVM);
        return RestResponse.ok(vm);
    }

    /**
     * 记录答题记录
     * @param model
     * @return com.mindskip.xzs.base.RestResponse<java.lang.String>
     * @throws Exception
     */
    @RequestMapping(value = "/practice/record", method = RequestMethod.POST)
    public RestResponse<String> practiceRecord(@RequestBody QuestionAnswerRecordRequestVM model) {
        String key = "PRACTICE:RECORD:" + getCurrentUser().getId() + ":" + model.getLevel() + ":" + model.getSubjectId();
        redisUtils.set(key, model.getQuestionId());
        model.setCreateUser(getCurrentUser().getId());
        eventPublisher.publishEvent(new SubmitAnswerEvent(model));
        return RestResponse.ok(null);
    }

    /**
     * 查看上次练习答题的记录
     * @param model
     * @return com.mindskip.xzs.base.RestResponse<java.lang.Integer>
     * @throws Exception
     */
    @RequestMapping(value = "/practice/viewRecord", method = RequestMethod.POST)
    public RestResponse<Object> practiceViewRecord(@RequestBody QuestionAnswerRecordRequestVM model) {
        String key = "PRACTICE:RECORD:" + getCurrentUser().getId() + ":" + model.getLevel() + ":" + model.getSubjectId();
        return RestResponse.ok(redisUtils.get(key));
    }

}
