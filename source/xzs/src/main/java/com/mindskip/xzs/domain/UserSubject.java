package com.mindskip.xzs.domain;

import java.io.Serializable;
import java.util.Date;

public class UserSubject implements Serializable {


    private static final long serialVersionUID = 3566048309749422079L;

    private Integer id;
    private Integer userId;
    private Integer levelId;
    private Integer subjectId;

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

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }
}
