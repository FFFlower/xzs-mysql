package com.mindskip.xzs.controller.ftl;

import com.mindskip.xzs.domain.enums.QuestionTypeEnum;
import com.mindskip.xzs.service.QuestionService;
import com.mindskip.xzs.service.TextContentService;
import com.mindskip.xzs.utility.EsPage;
import com.mindskip.xzs.viewmodel.admin.question.QuestionPageRequestEsVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p><freemarker模版>
 * <p><功能详细描述>
 * <p>Copyright (c) 2021/12/31, wangyu@zhuofansoft.com All Rights Reserve</p>
 *
 * @author wangyu
 * @version [V1.0, 2021/12/31]
 * @see [相关类/方法]
 */
@Controller
public class IndexController {

    private final QuestionService questionService;
    private final TextContentService textContentService;

    @Autowired
    public IndexController(QuestionService questionService, TextContentService textContentService) {
        this.questionService = questionService;
        this.textContentService = textContentService;
    }


    @RequestMapping(value = "index")
    public String index(Model model, QuestionPageRequestEsVM vm) {
        if (null == vm.getPageIndex()) {
            vm.setPageIndex(0);
        }
        if (null == vm.getPageSize()) {
            vm.setPageSize(10);
        }
        EsPage pageInfo = questionService.pageForEs(vm);
        pageInfo.getRecordList().forEach(x->x.put("questionTypeName", QuestionTypeEnum.fromCode((Integer) x.get("questionType")).getName()));
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("title", vm.getTitle());
        return "freemarker/search";
    }

}
