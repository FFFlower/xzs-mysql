package com.mindskip.xzs.viewmodel.admin.question;

import com.mindskip.xzs.base.BasePage;



public class QuestionPageRequestVM extends BasePage {

    private Integer id;
    private Integer level;
    private Integer subjectId;
    private Integer questionType;
    private Integer currentIdStart;
    private Integer limit;
    private Integer currentIdEnd;

    private String content;
    private String orderBy;

    private String  sort;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getQuestionType() {
        return questionType;
    }

    public void setQuestionType(Integer questionType) {
        this.questionType = questionType;
    }

    public Integer getCurrentIdStart() {
        return currentIdStart;
    }

    public void setCurrentIdStart(Integer currentIdStart) {
        this.currentIdStart = currentIdStart;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setOrderBy(String orderBy,String sort) {
        this.orderBy = orderBy;
        this.sort = sort;
    }

    public String orderBy() {
        return this.orderBy + " " + this.sort;
    }

    public Integer getCurrentIdEnd() {
        return currentIdEnd;
    }

    public void setCurrentIdEnd(Integer currentIdEnd) {
        this.currentIdEnd = currentIdEnd;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
