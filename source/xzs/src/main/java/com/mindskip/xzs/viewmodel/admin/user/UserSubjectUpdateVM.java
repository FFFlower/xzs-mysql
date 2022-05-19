package com.mindskip.xzs.viewmodel.admin.user;


import java.util.List;

public class UserSubjectUpdateVM {

    private Integer userId;

    private List<Integer> subjectIds;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<Integer> getSubjectIds() {
        return subjectIds;
    }

    public void setSubjectIds(List<Integer> subjectIds) {
        this.subjectIds = subjectIds;
    }
}
