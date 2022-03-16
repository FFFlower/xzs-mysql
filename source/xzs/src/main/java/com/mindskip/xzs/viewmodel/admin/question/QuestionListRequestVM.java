package com.mindskip.xzs.viewmodel.admin.question;

import java.util.List;

public class QuestionListRequestVM  {

    private Integer id;
    private Integer level;
    private Integer subjectId;
    private Integer questionType;
    private List<Integer> notInIds;

    private Integer limit;

    private String orderBy;

    private Integer preId;

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

    public List<Integer> getNotInIds() {
        return notInIds;
    }

    public void setNotInIds(List<Integer> notInIds) {
        this.notInIds = notInIds;
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

    public void setOrderBy(String orderBy,String sort) {
        this.orderBy = orderBy;
        this.sort = sort;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getPreId() {
        return preId;
    }

    public void setPreId(Integer preId) {
        this.preId = preId;
    }
}
