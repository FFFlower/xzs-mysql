package com.mindskip.xzs.domain.question;

import lombok.Data;

import java.io.Serializable;

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
public class QuestionEsItemObject implements Serializable {

    private static final long serialVersionUID = 2605842156590904967L;
    private String prefix;
    private String content;
    private String score;
    private String itemUuid;

}
