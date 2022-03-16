package com.mindskip.xzs.viewmodel.student.question.answer;

import lombok.Data;

@Data
public class QuestionAnswerRecordRequestVM {

    private Integer questionId;

    private Integer level;

    private Integer subjectId;

    private String answer;

    private Integer createUser;
}
