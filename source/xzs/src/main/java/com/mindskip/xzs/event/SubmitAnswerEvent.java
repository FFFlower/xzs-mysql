package com.mindskip.xzs.event;

import com.mindskip.xzs.viewmodel.student.question.answer.QuestionAnswerRecordRequestVM;
import org.springframework.context.ApplicationEvent;

/**
 * <p><模拟练习提交答案事件-记录错题>
 * <p><功能详细描述>
 * <p>Copyright (c) 2021/12/25, wangyu@zhuofansoft.com All Rights Reserve</p>
 *
 * @author wangyu
 * @version [V1.0, 2021/12/25]
 * @see [相关类/方法]
 */
public class SubmitAnswerEvent extends ApplicationEvent {

    private final QuestionAnswerRecordRequestVM requestVM;

    public SubmitAnswerEvent(final QuestionAnswerRecordRequestVM requestVM) {
        super(requestVM);
        this.requestVM = requestVM;
    }

    public QuestionAnswerRecordRequestVM getRequestVM() {
        return requestVM;
    }
}
