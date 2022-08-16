package com.mindskip.xzs.viewmodel.student.exam;

import com.mindskip.xzs.base.BasePage;

import javax.validation.constraints.NotNull;
import java.util.List;

public class ExamPaperPageVM extends BasePage {
    @NotNull
    private Integer paperType;
    private Integer subjectId;
    private Integer levelId;
    private Integer userId;

    private List<Integer> examPaperIds;

    private String name;

    public Integer getPaperType() {
        return paperType;
    }

    public void setPaperType(Integer paperType) {
        this.paperType = paperType;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getExamPaperIds() {
        return examPaperIds;
    }

    public void setExamPaperIds(List<Integer> examPaperIds) {
        this.examPaperIds = examPaperIds;
    }
}
