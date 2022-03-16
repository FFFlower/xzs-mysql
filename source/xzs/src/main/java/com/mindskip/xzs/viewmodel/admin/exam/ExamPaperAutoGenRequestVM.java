package com.mindskip.xzs.viewmodel.admin.exam;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ExamPaperAutoGenRequestVM {

    @NotNull
    private Integer level;
    @NotNull
    private Integer subjectId;
    @NotNull
    private Integer setMethod;

    private String name;
    @NotNull
    private Integer questionNum;
    @NotNull
    private Integer singleChoiceNum;
    @NotNull
    private Integer judgeNum;

    private Integer paperType;

}
