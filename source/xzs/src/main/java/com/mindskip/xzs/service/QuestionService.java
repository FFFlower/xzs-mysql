package com.mindskip.xzs.service;

import com.mindskip.xzs.domain.Question;
import com.mindskip.xzs.domain.question.QuestionEsObject;
import com.mindskip.xzs.utility.EsPage;
import com.mindskip.xzs.viewmodel.admin.question.QuestionEditRequestVM;
import com.mindskip.xzs.viewmodel.admin.question.QuestionPageRequestEsVM;
import com.mindskip.xzs.viewmodel.admin.question.QuestionPageRequestVM;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface QuestionService extends BaseService<Question> {

    PageInfo<Question> page(QuestionPageRequestVM requestVM);

    PageInfo<Question> pageByContent(QuestionPageRequestVM requestVM);

    Question insertFullQuestion(QuestionEditRequestVM model, Integer userId);

    void addEsData(QuestionEditRequestVM model);

    void updateEsData(QuestionEditRequestVM model);

    Question updateFullQuestion(QuestionEditRequestVM model);

    Question get(Integer id);

    QuestionEditRequestVM getQuestionEditRequestVM(Integer questionId);

    QuestionEditRequestVM getQuestionEditRequestVM(Question question);

    /**
     * 获取练习题
     * @param gradeLevel 作业类别
     * @param subjectId 准操项目
     * @param preId 上一题id
     * @return com.mindskip.xzs.viewmodel.admin.question.QuestionEditRequestVM
     * @throws Exception
     */
    QuestionEditRequestVM getQuestionEditRequestVM(Integer gradeLevel,Integer subjectId,Integer preId);

    Integer selectAllCount();

    Integer selectCount(QuestionPageRequestVM requestVM);

    List<Integer> selectMothCount();

    void batchDelete(Integer[] ids);

    void readExcel(MultipartFile file,Integer level,Integer subjectId, Integer userId);

    void initEsIndex() throws IOException;

    EsPage pageForEs(QuestionPageRequestEsVM model);

    void delByCondition(Integer level, Integer subjectId);
}
