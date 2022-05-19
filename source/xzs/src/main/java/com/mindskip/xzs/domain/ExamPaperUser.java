package com.mindskip.xzs.domain;

import java.io.Serializable;

public class ExamPaperUser implements Serializable {

    private static final long serialVersionUID = 6666101563687248566L;

    private Integer id;
    private Integer userId;
    private Integer examPaperId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getExamPaperId() {
        return examPaperId;
    }

    public void setExamPaperId(Integer examPaperId) {
        this.examPaperId = examPaperId;
    }
}
