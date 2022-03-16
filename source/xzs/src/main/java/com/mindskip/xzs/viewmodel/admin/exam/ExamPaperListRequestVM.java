package com.mindskip.xzs.viewmodel.admin.exam;

public class ExamPaperListRequestVM {

    private Integer id;
    private Integer subjectId;
    private Integer level;
    private Integer paperType;
    private Integer taskExamId;
    private Integer setMethod;

    public ExamPaperListRequestVM() {
    }

    public ExamPaperListRequestVM(Integer level, Integer subjectId, Integer paperType,Integer setMethod) {
        this.subjectId = subjectId;
        this.level = level;
        this.paperType = paperType;
        this.setMethod = setMethod;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getPaperType() {
        return paperType;
    }

    public void setPaperType(Integer paperType) {
        this.paperType = paperType;
    }

    public Integer getTaskExamId() {
        return taskExamId;
    }

    public void setTaskExamId(Integer taskExamId) {
        this.taskExamId = taskExamId;
    }

    public Integer getSetMethod() {
        return setMethod;
    }

    public void setSetMethod(Integer setMethod) {
        this.setMethod = setMethod;
    }
}
