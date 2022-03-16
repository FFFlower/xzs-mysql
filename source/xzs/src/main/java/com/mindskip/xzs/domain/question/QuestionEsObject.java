package com.mindskip.xzs.domain.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p><一句话功能简述>
 * <p><功能详细描述>
 * <p>Copyright (c) 2022/1/2, wangyu@zhuofansoft.com All Rights Reserve</p>
 *
 * @author wangyu
 * @version [V1.0, 2022/1/2]
 * @see [相关类/方法]
 */
@Data
public class QuestionEsObject implements Serializable {

    private static final long serialVersionUID = -6243204303656051403L;

    private Integer id;
    private Integer questionType;
    private Integer subjectId;
    private String title;
    private List<QuestionEsItemObject> items;
    private String analyze;
    private String correct;
    private String score;
    private Integer itemOrder;
}
