package com.mindskip.xzs.viewmodel.student.error;

import com.mindskip.xzs.domain.question.QuestionObject;
import lombok.Data;

import java.util.Date;

/**
 * <p><错题>
 * <p><功能详细描述>
 * <p>Copyright (c) 2021/12/26, wangyu@zhuofansoft.com All Rights Reserve</p>
 *
 * @author wangyu
 * @version [V1.0, 2021/12/26]
 * @see [相关类/方法]
 */
@Data
public class ExamErrorQuestionStudentResponseVM {

    private Integer id;

    /**
     * 题型
     */
    private Integer questionType;

    /**
     * 学科
     */
    private Integer subjectId;

    /**
     * 做题答案
     */
    private String answer;

    /**
     * 做题人
     */
    private Integer createUser;

    private String createTime;

    private Integer itemOrder;

    private String subjectName;

    private String paperName;

    private Boolean doRight;

    private QuestionObject question;


}
