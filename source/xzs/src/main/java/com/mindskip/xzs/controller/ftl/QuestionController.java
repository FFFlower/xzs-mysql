package com.mindskip.xzs.controller.ftl;

import com.mindskip.xzs.base.BaseApiController;
import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.service.QuestionService;
import com.mindskip.xzs.service.TextContentService;
import com.mindskip.xzs.utility.EsPage;
import com.mindskip.xzs.viewmodel.admin.question.QuestionPageRequestEsVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController("IndexQuestionController")
@RequestMapping(value = "/api/ftl/student/question")
public class QuestionController extends BaseApiController {

    private final QuestionService questionService;
    private final TextContentService textContentService;

    @Autowired
    public QuestionController(QuestionService questionService, TextContentService textContentService) {
        this.questionService = questionService;
        this.textContentService = textContentService;
    }

    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public RestResponse<EsPage> page(@RequestBody QuestionPageRequestEsVM model) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        System.out.println(request.getSession().getMaxInactiveInterval());
        EsPage pageInfo = questionService.pageForEs(model);
        return RestResponse.ok(pageInfo);
    }

    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public RestResponse<String> page() throws IOException {
        questionService.initEsIndex();
        return RestResponse.ok(null);
    }

}
