package com.mindskip.xzs.viewmodel.student.exampaper;

import com.mindskip.xzs.base.BasePage;

public class ExamPaperAnswerPageVM extends BasePage {

    private Integer subjectId;

    private Integer paperType;

    private Integer createUser;

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Integer createUser) {
        this.createUser = createUser;
    }

    public Integer getPaperType() {
        return paperType;
    }

    public void setPaperType(Integer paperType) {
        this.paperType = paperType;
    }
}
