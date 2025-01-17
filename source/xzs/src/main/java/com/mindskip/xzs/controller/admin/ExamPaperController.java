package com.mindskip.xzs.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.mindskip.xzs.base.BaseApiController;
import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.domain.ExamPaper;
import com.mindskip.xzs.domain.ExamPaperUser;
import com.mindskip.xzs.domain.enums.ExamPaperTypeEnum;
import com.mindskip.xzs.service.ExamPaperService;
import com.mindskip.xzs.service.ExamPaperUserService;
import com.mindskip.xzs.service.UserService;
import com.mindskip.xzs.utility.DateTimeUtil;
import com.mindskip.xzs.utility.PageInfoHelper;
import com.mindskip.xzs.viewmodel.admin.exam.ExamPaperAutoGenRequestVM;
import com.mindskip.xzs.viewmodel.admin.exam.ExamPaperPageRequestVM;
import com.mindskip.xzs.viewmodel.admin.exam.ExamPaperEditRequestVM;
import com.mindskip.xzs.viewmodel.admin.exam.ExamResponseVM;
import com.github.pagehelper.PageInfo;
import com.mindskip.xzs.viewmodel.admin.user.UserResponseVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController("AdminExamPaperController")
@RequestMapping(value = "/api/admin/exam/paper")
public class ExamPaperController extends BaseApiController {

    private final ExamPaperService examPaperService;
    private final ExamPaperUserService examPaperUsrService;
    private final UserService userService;

    @Autowired
    public ExamPaperController(ExamPaperService examPaperService,ExamPaperUserService examPaperUsrService,UserService userService) {
        this.examPaperService = examPaperService;
        this.examPaperUsrService = examPaperUsrService;
        this.userService = userService;
    }

    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public RestResponse<PageInfo<ExamResponseVM>> pageList(@RequestBody ExamPaperPageRequestVM model) {
        model.setPaperType(ExamPaperTypeEnum.Fixed.getCode());
        PageInfo<ExamPaper> pageInfo = examPaperService.page(model);
        List<Integer> examPaperIds = pageInfo.getList().stream().map(x -> x.getId()).collect(Collectors.toList());
        List<ExamPaperUser> examPaperUserList =  examPaperUsrService.findByPaperIds(examPaperIds);
        List<Integer> userIds = examPaperUserList.stream().map(x -> x.getUserId()).collect(Collectors.toList());
        Map<Integer,UserResponseVM> userResponseMap = userService.selectByIds(userIds).stream().map(x -> UserResponseVM.from(x)).collect(Collectors.toMap(x->x.getId(), x->x));
        PageInfo<ExamResponseVM> page = PageInfoHelper.copyMap(pageInfo, e -> {
            ExamResponseVM vm = modelMapper.map(e, ExamResponseVM.class);
            vm.setCreateTime(DateTimeUtil.dateFormat(e.getCreateTime()));
            vm.setStudents(examPaperUserList.stream().filter(x->x.getExamPaperId().intValue() == e.getId().intValue()).map(x->userResponseMap.get(x.getUserId())).collect(Collectors.toList()));
            return vm;
        });
        return RestResponse.ok(page);
    }



    @RequestMapping(value = "/taskExamPage", method = RequestMethod.POST)
    public RestResponse<PageInfo<ExamResponseVM>> taskExamPageList(@RequestBody ExamPaperPageRequestVM model) {
        PageInfo<ExamPaper> pageInfo = examPaperService.taskExamPage(model);
        PageInfo<ExamResponseVM> page = PageInfoHelper.copyMap(pageInfo, e -> {
            ExamResponseVM vm = modelMapper.map(e, ExamResponseVM.class);
            vm.setCreateTime(DateTimeUtil.dateFormat(e.getCreateTime()));
            return vm;
        });
        return RestResponse.ok(page);
    }



    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public RestResponse<ExamPaperEditRequestVM> edit(@RequestBody @Valid ExamPaperEditRequestVM model) {
        ExamPaper examPaper = examPaperService.savePaperFromVM(model, getCurrentUser());
        ExamPaperEditRequestVM newVM = examPaperService.examPaperToVM(examPaper.getId());
        return RestResponse.ok(newVM);
    }

    @RequestMapping(value = "/autoGenerate", method = RequestMethod.POST)
    public RestResponse<ExamPaperAutoGenRequestVM> autoGen(@RequestBody @Valid ExamPaperAutoGenRequestVM model) {
        ExamPaper examPaper = examPaperService.savePaperFromAuto(model, getCurrentUser());
        if (null == examPaper) {
            return RestResponse.fail(2, "暂无试题，请联系管理员添加");
        }
        return RestResponse.ok();
    }

    @RequestMapping(value = "/select/{id}", method = RequestMethod.POST)
    public RestResponse<ExamPaperEditRequestVM> select(@PathVariable Integer id) {
        ExamPaperEditRequestVM vm = examPaperService.examPaperToVM(id);
        return RestResponse.ok(vm);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public RestResponse delete(@PathVariable Integer id) {
        ExamPaper examPaper = examPaperService.selectById(id);
        examPaper.setDeleted(true);
        examPaperService.updateByIdFilter(examPaper);
        return RestResponse.ok();
    }
}
