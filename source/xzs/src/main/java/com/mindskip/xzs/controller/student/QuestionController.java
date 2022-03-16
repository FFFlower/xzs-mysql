package com.mindskip.xzs.controller.student;

import com.github.pagehelper.PageInfo;
import com.mindskip.xzs.base.BaseApiController;
import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.domain.Question;
import com.mindskip.xzs.domain.TextContent;
import com.mindskip.xzs.domain.enums.QuestionTypeEnum;
import com.mindskip.xzs.domain.question.QuestionObject;
import com.mindskip.xzs.service.QuestionService;
import com.mindskip.xzs.service.TextContentService;
import com.mindskip.xzs.utility.*;
import com.mindskip.xzs.viewmodel.admin.question.QuestionEditRequestVM;
import com.mindskip.xzs.viewmodel.admin.question.QuestionPageRequestVM;
import com.mindskip.xzs.viewmodel.admin.question.QuestionResponseVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController("StudentQuestionController")
@RequestMapping(value = "/api/student/question")
public class QuestionController extends BaseApiController {

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
        return RestResponse.ok(page);
    }

    @RequestMapping(value = "/count", method = RequestMethod.POST)
    public RestResponse<Map<String, Integer>> count(@RequestBody QuestionPageRequestVM model) {
        Integer count = questionService.selectCount(model);
        model.setQuestionType(QuestionTypeEnum.SingleChoice.getCode());
        Integer singleCount = questionService.selectCount(model);
        model.setQuestionType(QuestionTypeEnum.TrueFalse.getCode());
        Integer judgeCount = questionService.selectCount(model);
        Map<String, Integer> responseData = new HashMap<>();
        responseData.put("count", count);
        responseData.put("singleCount", singleCount);
        responseData.put("judgeCount", judgeCount);
        return RestResponse.ok(responseData);
    }

}
