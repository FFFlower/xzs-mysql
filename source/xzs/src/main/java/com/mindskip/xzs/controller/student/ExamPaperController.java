package com.mindskip.xzs.controller.student;

import com.mindskip.xzs.base.BaseApiController;
import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.domain.ExamPaper;
import com.mindskip.xzs.domain.ExamPaperUser;
import com.mindskip.xzs.domain.Subject;
import com.mindskip.xzs.domain.User;
import com.mindskip.xzs.domain.enums.ExamPaperMethodEnum;
import com.mindskip.xzs.domain.enums.ExamPaperTypeEnum;
import com.mindskip.xzs.service.ExamPaperAnswerService;
import com.mindskip.xzs.service.ExamPaperService;
import com.mindskip.xzs.service.ExamPaperUserService;
import com.mindskip.xzs.service.SubjectService;
import com.mindskip.xzs.utility.DateTimeUtil;
import com.mindskip.xzs.utility.PageInfoHelper;
import com.mindskip.xzs.viewmodel.admin.exam.ExamPaperAutoGenRequestVM;
import com.mindskip.xzs.viewmodel.admin.exam.ExamPaperEditRequestVM;
import com.mindskip.xzs.viewmodel.student.exam.ExamPaperPageResponseVM;
import com.mindskip.xzs.viewmodel.student.exam.ExamPaperPageVM;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController("StudentExamPaperController")
@RequestMapping(value = "/api/student/exam/paper")
public class ExamPaperController extends BaseApiController {

    private final ExamPaperService examPaperService;
    private final ExamPaperAnswerService examPaperAnswerService;
    private final ApplicationEventPublisher eventPublisher;
    private final SubjectService subjectService;

    private final ExamPaperUserService examPaperUserService;

    @Autowired
    public ExamPaperController(ExamPaperService examPaperService, ExamPaperAnswerService examPaperAnswerService, ApplicationEventPublisher eventPublisher, SubjectService subjectService, ExamPaperUserService examPaperUserService) {
        this.examPaperService = examPaperService;
        this.examPaperAnswerService = examPaperAnswerService;
        this.eventPublisher = eventPublisher;
        this.subjectService = subjectService;
        this.examPaperUserService = examPaperUserService;
    }


    @RequestMapping(value = "/select/{id}", method = RequestMethod.POST)
    public RestResponse<ExamPaperEditRequestVM> select(@PathVariable Integer id) {
        ExamPaperEditRequestVM vm = examPaperService.examPaperToVM(id);
        return RestResponse.ok(vm);
    }


    @RequestMapping(value = "/pageList", method = RequestMethod.POST)
    public RestResponse<PageInfo<ExamPaperPageResponseVM>> pageList(@RequestBody @Valid ExamPaperPageVM model) {
        User user = getCurrentUser();
        if (ExamPaperTypeEnum.IntelligenceTrain.getCode() == model.getPaperType()) {
            model.setUserId(user.getId());
        } else {
            List<ExamPaperUser> examPaperUserList = examPaperUserService.findByUserId(user.getId());
            List<Integer> examPaperIds = examPaperUserList.stream().map(x->x.getExamPaperId()).collect(Collectors.toList());
            model.setExamPaperIds(examPaperIds);
        }
        PageInfo<ExamPaper> pageInfo = examPaperService.studentPage(model);
        PageInfo<ExamPaperPageResponseVM> page = PageInfoHelper.copyMap(pageInfo, e -> {
            ExamPaperPageResponseVM vm = modelMapper.map(e, ExamPaperPageResponseVM.class);
            vm.setCreateTime(DateTimeUtil.dateFormat(e.getCreateTime()));
            return vm;
        });
        return RestResponse.ok(page);
    }

    @RequestMapping(value = "/autoGenerate", method = RequestMethod.POST)
    public RestResponse<ExamPaperAutoGenRequestVM> autoGen(@RequestBody @Valid ExamPaperAutoGenRequestVM model) {
        model.setPaperType(ExamPaperTypeEnum.IntelligenceTrain.getCode());
        model.setSetMethod(ExamPaperMethodEnum.Random.getCode());
        model.setName("智能训练"+DateTimeUtil.dateNoSymbolFormat(new Date())+"-"+getCurrentUser().getRealName());
        ExamPaper examPaper = examPaperService.savePaperFromAuto(model, getCurrentUser());
        if (null == examPaper) {
            return RestResponse.fail(2, "暂无试题，请联系管理员添加");
        }
        return RestResponse.ok();
    }
}
