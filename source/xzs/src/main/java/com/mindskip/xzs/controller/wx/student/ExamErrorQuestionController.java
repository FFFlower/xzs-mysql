package com.mindskip.xzs.controller.wx.student;

import com.github.pagehelper.PageInfo;
import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.controller.wx.BaseWXApiController;
import com.mindskip.xzs.domain.*;
import com.mindskip.xzs.domain.question.QuestionObject;
import com.mindskip.xzs.event.SubmitAnswerEvent;
import com.mindskip.xzs.service.*;
import com.mindskip.xzs.utility.*;
import com.mindskip.xzs.viewmodel.admin.question.QuestionEditRequestVM;
import com.mindskip.xzs.viewmodel.student.error.ExamErrorQuestionStudentResponseVM;
import com.mindskip.xzs.viewmodel.student.exam.ExamPaperSubmitItemVM;
import com.mindskip.xzs.viewmodel.student.question.answer.QuestionAnswerRecordRequestVM;
import com.mindskip.xzs.viewmodel.student.question.answer.QuestionAnswerVM;
import com.mindskip.xzs.viewmodel.student.question.answer.QuestionPageStudentRequestVM;
import com.mindskip.xzs.viewmodel.student.question.answer.QuestionPageStudentResponseVM;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@RestController("WXStudentExamErrorQuestionController")
@RequiredArgsConstructor
@RequestMapping(value = "/api/wx/student/exam/error")
public class ExamErrorQuestionController extends BaseWXApiController {

    private final ExamErrorQuestionService examErrorQuestionService;
    private final QuestionService questionService;
    private final TextContentService textContentService;
    private final SubjectService subjectService;
    private final ExamPaperService examPaperService;

    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public RestResponse<PageInfo<ExamErrorQuestionStudentResponseVM>> pageList(@RequestBody QuestionPageStudentRequestVM model) {
        model.setCreateUser(getCurrentUser().getId());
        PageInfo<ExamErrorQuestion> pageInfo = examErrorQuestionService.studentPage(model);
        PageInfo<ExamErrorQuestionStudentResponseVM> page = PageInfoHelper.copyMap(pageInfo, q -> {
            Subject subject = subjectService.selectById(q.getSubjectId());
            ExamErrorQuestionStudentResponseVM vm = modelMapper.map(q, ExamErrorQuestionStudentResponseVM.class);
            vm.setCreateTime(DateTimeUtil.dateFormat(q.getCreateTime()));
            TextContent textContent = textContentService.selectById(q.getQuestionTextContentId());
            QuestionObject questionObject = JsonUtil.toJsonObject(textContent.getContent(), QuestionObject.class);
            vm.setQuestion(questionObject);
            vm.setPaperName(null == q.getExamPaperId() ? "题库练习" : examPaperService.get(q.getExamPaperId()).getName());
            vm.setSubjectName(subject.getName());
            return vm;
        });
        return RestResponse.ok(page);
    }

    @RequestMapping(value = "/del/{id}", method = RequestMethod.POST)
    public RestResponse<String> practiceRecord(@PathVariable Integer id) {
        examErrorQuestionService.deleteByPrimaryKey(id);
        return RestResponse.ok(null);
    }

}
