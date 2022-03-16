package com.mindskip.xzs.viewmodel.student.exam;

import com.mindskip.xzs.domain.question.QuestionObject;
import lombok.Data;

/**
 * <p><一句话功能简述>
 * <p><功能详细描述>
 * <p>Copyright (c) 2021/12/26, wangyu@zhuofansoft.com All Rights Reserve</p>
 *
 * @author wangyu
 * @version [V1.0, 2021/12/26]
 * @see [相关类/方法]
 */
@Data
public class ExamPaperQuestionReadVM {

    private Integer id;

    private Integer questionType;

    private Integer itemOrder;

    private String answer;

    private Integer createUser;

    private String createTime;

    private QuestionObject question;
}
