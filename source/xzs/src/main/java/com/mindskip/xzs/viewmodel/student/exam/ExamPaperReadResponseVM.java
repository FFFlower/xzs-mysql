package com.mindskip.xzs.viewmodel.student.exam;

import com.mindskip.xzs.domain.question.QuestionObject;
import lombok.Data;

import java.util.List;

/**
 * <p><考试答案>
 * <p><功能详细描述>
 * <p>Copyright (c) 2021/12/26, wangyu@zhuofansoft.com All Rights Reserve</p>
 *
 * @author wangyu
 * @version [V1.0, 2021/12/26]
 * @see [相关类/方法]
 */
@Data
public class ExamPaperReadResponseVM {

    private Integer id;

    private String paperName;

    private String doTime;

    private Integer questionCount;

    private String userScore;

    private String score;

    private String subjectName;

    private String createTime;

    private List<ExamPaperQuestionReadVM> examAnswers;


}
