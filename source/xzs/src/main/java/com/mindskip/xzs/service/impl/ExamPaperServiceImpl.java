package com.mindskip.xzs.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.mindskip.xzs.domain.TextContent;
import com.mindskip.xzs.domain.enums.ExamPaperMethodEnum;
import com.mindskip.xzs.domain.enums.ExamPaperTypeEnum;
import com.mindskip.xzs.domain.enums.QuestionTypeEnum;
import com.mindskip.xzs.domain.exam.ExamPaperQuestionItemObject;
import com.mindskip.xzs.domain.exam.ExamPaperTitleItemObject;
import com.mindskip.xzs.domain.other.KeyValue;
import com.mindskip.xzs.repository.BaseMapper;
import com.mindskip.xzs.repository.ExamPaperMapper;
import com.mindskip.xzs.repository.QuestionMapper;
import com.mindskip.xzs.service.ExamPaperService;
import com.mindskip.xzs.service.QuestionService;
import com.mindskip.xzs.service.SubjectService;
import com.mindskip.xzs.service.TextContentService;
import com.mindskip.xzs.service.enums.ActionEnum;
import com.mindskip.xzs.utility.DateTimeUtil;
import com.mindskip.xzs.utility.JsonUtil;
import com.mindskip.xzs.utility.ModelMapperSingle;
import com.mindskip.xzs.utility.ExamUtil;
import com.mindskip.xzs.viewmodel.admin.exam.*;
import com.mindskip.xzs.viewmodel.admin.question.QuestionEditRequestVM;
import com.mindskip.xzs.viewmodel.admin.question.QuestionListRequestVM;
import com.mindskip.xzs.viewmodel.student.dashboard.PaperFilter;
import com.mindskip.xzs.viewmodel.student.dashboard.PaperInfo;
import com.mindskip.xzs.viewmodel.student.exam.ExamPaperPageVM;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mindskip.xzs.domain.ExamPaper;
import com.mindskip.xzs.domain.Question;
import com.mindskip.xzs.domain.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ExamPaperServiceImpl extends BaseServiceImpl<ExamPaper> implements ExamPaperService {

    protected final static ModelMapper modelMapper = ModelMapperSingle.Instance();
    private final ExamPaperMapper examPaperMapper;
    private final QuestionMapper questionMapper;
    private final TextContentService textContentService;
    private final QuestionService questionService;
    private final SubjectService subjectService;

    @Autowired
    public ExamPaperServiceImpl(ExamPaperMapper examPaperMapper, QuestionMapper questionMapper, TextContentService textContentService, QuestionService questionService, SubjectService subjectService) {
        super(examPaperMapper);
        this.examPaperMapper = examPaperMapper;
        this.questionMapper = questionMapper;
        this.textContentService = textContentService;
        this.questionService = questionService;
        this.subjectService = subjectService;
    }


    @Override
    public PageInfo<ExamPaper> page(ExamPaperPageRequestVM requestVM) {
        return PageHelper.startPage(requestVM.getPageIndex(), requestVM.getPageSize(), "id desc").doSelectPageInfo(() ->
                examPaperMapper.page(requestVM));
    }

    @Override
    public PageInfo<ExamPaper> taskExamPage(ExamPaperPageRequestVM requestVM) {
        return PageHelper.startPage(requestVM.getPageIndex(), requestVM.getPageSize(), "id desc").doSelectPageInfo(() ->
                examPaperMapper.taskExamPage(requestVM));
    }

    @Override
    public PageInfo<ExamPaper> studentPage(ExamPaperPageVM requestVM) {
        return PageHelper.startPage(requestVM.getPageIndex(), requestVM.getPageSize(), "id desc").doSelectPageInfo(() ->
                examPaperMapper.studentPage(requestVM));
    }


    @Override
    @Transactional
    @CacheEvict(value = "xzs:examPaper", key = "#examPaperEditRequestVM.id")
    public ExamPaper savePaperFromVM(ExamPaperEditRequestVM examPaperEditRequestVM, User user) {
        ActionEnum actionEnum = (examPaperEditRequestVM.getId() == null) ? ActionEnum.ADD : ActionEnum.UPDATE;
        Date now = new Date();
        List<ExamPaperTitleItemVM> titleItemsVM = examPaperEditRequestVM.getTitleItems();
        List<ExamPaperTitleItemObject> frameTextContentList = frameTextContentFromVM(titleItemsVM);
        String frameTextContentStr = JsonUtil.toJsonStr(frameTextContentList);

        ExamPaper examPaper;
        if (actionEnum == ActionEnum.ADD) {
            examPaper = modelMapper.map(examPaperEditRequestVM, ExamPaper.class);
            TextContent frameTextContent = new TextContent(frameTextContentStr, now);
            textContentService.insertByFilter(frameTextContent);
            examPaper.setFrameTextContentId(frameTextContent.getId());
            examPaper.setCreateTime(now);
            examPaper.setCreateUser(user.getId());
            examPaper.setDeleted(false);
            examPaperFromVM(examPaperEditRequestVM, examPaper, titleItemsVM);
            examPaperMapper.insertSelective(examPaper);
        } else {
            examPaper = examPaperMapper.selectByPrimaryKey(examPaperEditRequestVM.getId());
            TextContent frameTextContent = textContentService.selectById(examPaper.getFrameTextContentId());
            frameTextContent.setContent(frameTextContentStr);
            textContentService.updateByIdFilter(frameTextContent);
            modelMapper.map(examPaperEditRequestVM, examPaper);
            examPaperFromVM(examPaperEditRequestVM, examPaper, titleItemsVM);
            examPaperMapper.updateByPrimaryKeySelective(examPaper);
        }
        return examPaper;
    }

    @Override
    public ExamPaperEditRequestVM examPaperToVM(Integer id) {
        ExamPaper examPaper = examPaperMapper.selectByPrimaryKey(id);
        ExamPaperEditRequestVM vm = modelMapper.map(examPaper, ExamPaperEditRequestVM.class);
        vm.setLevel(examPaper.getGradeLevel());
        TextContent frameTextContent = textContentService.selectById(examPaper.getFrameTextContentId());
        List<ExamPaperTitleItemObject> examPaperTitleItemObjects = JsonUtil.toJsonListObject(frameTextContent.getContent(), ExamPaperTitleItemObject.class);
        List<Integer> questionIds = examPaperTitleItemObjects.stream()
                .flatMap(t -> t.getQuestionItems().stream()
                        .map(q -> q.getId()))
                .collect(Collectors.toList());
        List<Question> questions = questionMapper.selectByIds(questionIds);
        List<ExamPaperTitleItemVM> examPaperTitleItemVMS = examPaperTitleItemObjects.stream().map(t -> {
            ExamPaperTitleItemVM tTitleVM = modelMapper.map(t, ExamPaperTitleItemVM.class);
            List<QuestionEditRequestVM> questionItemsVM = t.getQuestionItems().stream().map(i -> {
                Question question = questions.stream().filter(q -> q.getId().equals(i.getId())).findFirst().get();
                QuestionEditRequestVM questionEditRequestVM = questionService.getQuestionEditRequestVM(question);
                questionEditRequestVM.setItemOrder(i.getItemOrder());
                return questionEditRequestVM;
            }).collect(Collectors.toList());
            tTitleVM.setQuestionItems(questionItemsVM);
            return tTitleVM;
        }).collect(Collectors.toList());
        vm.setTitleItems(examPaperTitleItemVMS);
        vm.setScore(ExamUtil.scoreToVM(examPaper.getScore()));
        if (ExamPaperTypeEnum.TimeLimit == ExamPaperTypeEnum.fromCode(examPaper.getPaperType())) {
            List<String> limitDateTime = Arrays.asList(DateTimeUtil.dateFormat(examPaper.getLimitStartTime()), DateTimeUtil.dateFormat(examPaper.getLimitEndTime()));
            vm.setLimitDateTime(limitDateTime);
        }
        return vm;
    }

    @Override
    public List<PaperInfo> indexPaper(PaperFilter paperFilter) {
        List<PaperInfo> result =  examPaperMapper.indexPaper(paperFilter);
        result.forEach(x -> x.setScore(x.getScore() / 10));
        return result;
    }


    @Override
    public Integer selectAllCount() {
        return examPaperMapper.selectAllCount();
    }

    @Override
    public List<Integer> selectMothCount() {
        Date startTime = DateTimeUtil.getMonthStartDay();
        Date endTime = DateTimeUtil.getMonthEndDay();
        List<KeyValue> mouthCount = examPaperMapper.selectCountByDate(startTime, endTime);
        List<String> mothStartToNowFormat = DateTimeUtil.MothStartToNowFormat();
        return mothStartToNowFormat.stream().map(md -> {
            KeyValue keyValue = mouthCount.stream().filter(kv -> kv.getName().equals(md)).findAny().orElse(null);
            return null == keyValue ? 0 : keyValue.getValue();
        }).collect(Collectors.toList());
    }

    @Override
    public ExamPaper savePaperFromAuto(ExamPaperAutoGenRequestVM model, User currentUser) {
        ExamPaperEditRequestVM examPaperVM = new ExamPaperEditRequestVM();
        examPaperVM.setSetMethod(model.getSetMethod());
        examPaperVM.setLevel(model.getLevel());
        examPaperVM.setSubjectId(model.getSubjectId());
        examPaperVM.setName(model.getName());
        examPaperVM.setPaperType(!StringUtils.isEmpty(model.getPaperType()) ? model.getPaperType() : ExamPaperTypeEnum.Fixed.getCode());
        examPaperVM.setSuggestTime(100);
        List<ExamPaperTitleItemVM> titleItemVMS = selectQuestion(model);
        if (CollectionUtils.isEmpty(titleItemVMS.get(0).getQuestionItems()) && CollectionUtils.isEmpty(titleItemVMS.get(1).getQuestionItems())) {
            return null;
        }
        examPaperVM.setTitleItems(titleItemVMS);
        return savePaperFromVM(examPaperVM,currentUser);
    }

    @Override
    @Cacheable(value = "xzs:examPaper", key = "#id", unless = "#result == null")
    public ExamPaper get(Integer id) {
        return examPaperMapper.selectByPrimaryKey(id);
    }

    private List<ExamPaperTitleItemVM> selectQuestion(ExamPaperAutoGenRequestVM model) {
        List<ExamPaperTitleItemVM> titleItems = new ArrayList<>();
        //单选题
        ExamPaperTitleItemVM singleTitleItem = new ExamPaperTitleItemVM();
        singleTitleItem.setName(QuestionTypeEnum.SingleChoice.getName());
        //判断题
        ExamPaperTitleItemVM judgeTitleItem = new ExamPaperTitleItemVM();
        judgeTitleItem.setName(QuestionTypeEnum.TrueFalse.getName());
        List<Question> singleList = new ArrayList<>();
        List<Question> judgeList = new ArrayList<>();
        if (ExamPaperMethodEnum.Order.getCode() == model.getSetMethod()) {
            //顺序抽题
            singleList = this.selectByOrder(model, QuestionTypeEnum.SingleChoice, model.getSingleChoiceNum());
            judgeList = this.selectByOrder(model, QuestionTypeEnum.TrueFalse, model.getJudgeNum());
        } else if (ExamPaperMethodEnum.Random.getCode() == model.getSetMethod()) {
            //随机抽题
            singleList = this.selectByRandom(model, QuestionTypeEnum.SingleChoice, model.getSingleChoiceNum());
            judgeList = this.selectByRandom(model, QuestionTypeEnum.TrueFalse, model.getJudgeNum());
        }
        List<QuestionEditRequestVM> singleItems = singleList.stream().map(x -> {
                    QuestionEditRequestVM vm = modelMapper.map(x, QuestionEditRequestVM.class);
                    vm.setScore(String.valueOf(x.getScore()/10));
                    return vm;
                }
            ).collect(Collectors.toList());
        List<QuestionEditRequestVM> judgeItems = judgeList.stream().map(x->{
                QuestionEditRequestVM vm = modelMapper.map(x, QuestionEditRequestVM.class);
                vm.setScore(String.valueOf(x.getScore()/10));
                return vm;
            }).collect(Collectors.toList());
        singleTitleItem.setQuestionItems(singleItems);
        judgeTitleItem.setQuestionItems(judgeItems);
        titleItems.add(singleTitleItem);
        titleItems.add(judgeTitleItem);
        return titleItems;
    }

    private List<Question> selectByRandom(ExamPaperAutoGenRequestVM model, QuestionTypeEnum questionType, Integer num) {
        if (num <= 0) {
            return new ArrayList<>();
        }
        QuestionListRequestVM condition = new QuestionListRequestVM();
        condition.setLevel(model.getLevel());
        condition.setSubjectId(model.getSubjectId());
        condition.setQuestionType(questionType.getCode());
        condition.setLimit(num);
        //随机
        condition.setOrderBy("Rand()");
        List<Question> questionList = questionMapper.selectByCondition(condition);
        return questionList;
    }

    private List<Question> selectByOrder(ExamPaperAutoGenRequestVM model, QuestionTypeEnum questionType, Integer num) {
        List<Integer> preQuestionIds = findPreQuestionIds(model.getLevel(),model.getSubjectId(),ExamPaperTypeEnum.Fixed.getCode());
        QuestionListRequestVM condition = new QuestionListRequestVM();
        condition.setLevel(model.getLevel());
        condition.setSubjectId(model.getSubjectId());
        condition.setQuestionType(questionType.getCode());
        condition.setNotInIds(preQuestionIds);
        condition.setLimit(num);
        condition.setOrderBy("id","asc");
        List<Question> questionList = questionMapper.selectByCondition(condition);
        questionList.addAll(selectByRandom(model,questionType,(num - questionList.size())));
        return questionList;
    }

    private List<Integer> findPreQuestionIds(Integer levelId,Integer subjectId,Integer paperType) {
        ExamPaperListRequestVM examPaperCondition = new ExamPaperListRequestVM(levelId,subjectId,paperType,ExamPaperMethodEnum.Order.getCode());
        List<ExamPaper> preExamPaperList = examPaperMapper.selectByCondition(examPaperCondition);
        List<Integer> frameContentIds = preExamPaperList.stream().map(x -> x.getFrameTextContentId()).collect(Collectors.toList());
        List<TextContent> contentList = textContentService.selectByIds(frameContentIds);
        List<ExamPaperTitleItemObject> titleItemObjectList = new ArrayList<>();
        for (TextContent textContent : contentList) {
            titleItemObjectList.addAll(JSONArray.parseArray(textContent.getContent(),ExamPaperTitleItemObject.class));
        }
        List<Integer> questionIds = new ArrayList<>();
        for (ExamPaperTitleItemObject examPaperTitleItemObject : titleItemObjectList) {
            questionIds.addAll(examPaperTitleItemObject.getQuestionItems().stream().map(x -> x.getId()).collect(Collectors.toList()));
        }
        return questionIds;
    }

    private void examPaperFromVM(ExamPaperEditRequestVM examPaperEditRequestVM, ExamPaper examPaper, List<ExamPaperTitleItemVM> titleItemsVM) {
        Integer gradeLevel = subjectService.levelBySubjectId(examPaperEditRequestVM.getSubjectId());
        Integer questionCount = titleItemsVM.stream()
                .mapToInt(t -> t.getQuestionItems().size()).sum();
        Integer score = titleItemsVM.stream().
                flatMapToInt(t -> t.getQuestionItems().stream()
                        .mapToInt(q -> ExamUtil.scoreFromVM(q.getScore()))
                ).sum();
        examPaper.setQuestionCount(questionCount);
        examPaper.setScore(score);
        examPaper.setGradeLevel(gradeLevel);
        List<String> dateTimes = examPaperEditRequestVM.getLimitDateTime();
        if (ExamPaperTypeEnum.TimeLimit == ExamPaperTypeEnum.fromCode(examPaper.getPaperType())) {
            examPaper.setLimitStartTime(DateTimeUtil.parse(dateTimes.get(0), DateTimeUtil.STANDER_FORMAT));
            examPaper.setLimitEndTime(DateTimeUtil.parse(dateTimes.get(1), DateTimeUtil.STANDER_FORMAT));
        }
    }

    private List<ExamPaperTitleItemObject> frameTextContentFromVM(List<ExamPaperTitleItemVM> titleItems) {
        AtomicInteger index = new AtomicInteger(1);
        return titleItems.stream().map(t -> {
            ExamPaperTitleItemObject titleItem = modelMapper.map(t, ExamPaperTitleItemObject.class);
            List<ExamPaperQuestionItemObject> questionItems = t.getQuestionItems().stream()
                    .map(q -> {
                        ExamPaperQuestionItemObject examPaperQuestionItemObject = modelMapper.map(q, ExamPaperQuestionItemObject.class);
                        examPaperQuestionItemObject.setItemOrder(index.getAndIncrement());
                        return examPaperQuestionItemObject;
                    })
                    .collect(Collectors.toList());
            titleItem.setQuestionItems(questionItems);
            return titleItem;
        }).collect(Collectors.toList());
    }
}
