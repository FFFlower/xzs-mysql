package com.mindskip.xzs.viewmodel.student.dashboard;


import java.util.Date;
import java.util.List;

public class PaperFilter {
    private Integer userId;
    private Date dateTime;
    private Integer examPaperType;
    private Integer gradeLevel;

    private List<Integer> examPaperIds;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getExamPaperType() {
        return examPaperType;
    }

    public void setExamPaperType(Integer examPaperType) {
        this.examPaperType = examPaperType;
    }

    public Integer getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(Integer gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public List<Integer> getExamPaperIds() {
        return examPaperIds;
    }

    public void setExamPaperIds(List<Integer> examPaperIds) {
        this.examPaperIds = examPaperIds;
    }
}
