package com.mindskip.xzs.viewmodel.admin.paper;

import com.mindskip.xzs.base.BasePage;

public class ExamPaperAnswerPageRequestVM extends BasePage {
    private Integer subjectId;
    private Integer paperType;

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getPaperType() {
        return paperType;
    }

    public void setPaperType(Integer paperType) {
        this.paperType = paperType;
    }
}
