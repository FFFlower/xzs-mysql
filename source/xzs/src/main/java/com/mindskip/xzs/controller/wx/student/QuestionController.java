package com.mindskip.xzs.controller.wx.student;

import com.github.pagehelper.PageInfo;
import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.controller.wx.BaseWXApiController;
import com.mindskip.xzs.domain.Question;
import com.mindskip.xzs.service.QuestionService;
import com.mindskip.xzs.service.TextContentService;
import com.mindskip.xzs.utility.PageInfoHelper;
import com.mindskip.xzs.viewmodel.admin.question.QuestionEditRequestVM;
import com.mindskip.xzs.viewmodel.admin.question.QuestionPageRequestVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("WXStudentQuestionController")
@RequestMapping(value = "/api/wx/student/question")
public class QuestionController extends BaseWXApiController {

    private final QuestionService questionService;
    private final TextContentService textContentService;

    @Autowired
    public QuestionController(QuestionService questionService, TextContentService textContentService) {
        this.questionService = questionService;
        this.textContentService = textContentService;
    }

    @RequestMapping(value = "/first/{levelId}/{subjectId}/{preId}", method = RequestMethod.POST)
    public RestResponse<QuestionEditRequestVM> first(@PathVariable Integer levelId, @PathVariable Integer subjectId, @PathVariable Integer preId) {
        QuestionEditRequestVM newVM = questionService.getQuestionEditRequestVM(levelId,subjectId,preId);
        return RestResponse.ok(newVM);
    }

    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public RestResponse<PageInfo<QuestionEditRequestVM>> page(@RequestBody QuestionPageRequestVM model) {
        PageInfo<Question> pageInfo = questionService.page(model);
        PageInfo<QuestionEditRequestVM> page = PageInfoHelper.copyMap(pageInfo, q -> questionService.getQuestionEditRequestVM(q));
        int curRow = page.getStartRow();
        for (QuestionEditRequestVM questionEditRequestVM : page.getList()) {
            questionEditRequestVM.setSort(curRow);
            curRow ++;
        }
        return RestResponse.ok(page);
    }

    @RequestMapping(value = "/count", method = RequestMethod.POST)
    public RestResponse<Integer> count(@RequestBody QuestionPageRequestVM model) {
        Integer count = questionService.selectCount(model);
        return RestResponse.ok(count);
    }

}
