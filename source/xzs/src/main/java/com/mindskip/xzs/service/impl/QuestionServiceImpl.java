package com.mindskip.xzs.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mindskip.xzs.configuration.property.FileProperties;
import com.mindskip.xzs.domain.other.KeyValue;
import com.mindskip.xzs.domain.Question;
import com.mindskip.xzs.domain.TextContent;
import com.mindskip.xzs.domain.enums.QuestionStatusEnum;
import com.mindskip.xzs.domain.enums.QuestionTypeEnum;
import com.mindskip.xzs.domain.question.QuestionEsItemObject;
import com.mindskip.xzs.domain.question.QuestionEsObject;
import com.mindskip.xzs.domain.question.QuestionItemObject;
import com.mindskip.xzs.domain.question.QuestionObject;
import com.mindskip.xzs.exception.BadRequestException;
import com.mindskip.xzs.exception.BusinessException;
import com.mindskip.xzs.repository.QuestionMapper;
import com.mindskip.xzs.service.QuestionService;
import com.mindskip.xzs.service.SubjectService;
import com.mindskip.xzs.service.TextContentService;
import com.mindskip.xzs.utility.*;
import com.mindskip.xzs.viewmodel.admin.question.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl extends BaseServiceImpl<Question> implements QuestionService {

    protected final static ModelMapper modelMapper = ModelMapperSingle.Instance();
    private final QuestionMapper questionMapper;
    private final TextContentService textContentService;
    private final SubjectService subjectService;
    private final FileProperties properties;

    @Autowired
    public QuestionServiceImpl(QuestionMapper questionMapper, TextContentService textContentService, SubjectService subjectService, FileProperties properties) {
        super(questionMapper);
        this.textContentService = textContentService;
        this.questionMapper = questionMapper;
        this.subjectService = subjectService;
        this.properties = properties;
    }

    @Override
    public PageInfo<Question> page(QuestionPageRequestVM requestVM) {
        return PageHelper.startPage(requestVM.getPageIndex(), requestVM.getPageSize(), StringUtils.isEmpty(requestVM.getOrderBy()) ? "id asc" : requestVM.orderBy()).doSelectPageInfo(() ->
                questionMapper.page(requestVM)
        );
    }

    @Override
    public PageInfo<Question> pageByContent(QuestionPageRequestVM requestVM) {
        return PageHelper.startPage(requestVM.getPageIndex(), requestVM.getPageSize(), StringUtils.isEmpty(requestVM.getOrderBy()) ? "id asc" : requestVM.orderBy()).doSelectPageInfo(() ->
                questionMapper.pageByContent(requestVM)
        );
    }


    @Override
    @Transactional
    public Question insertFullQuestion(QuestionEditRequestVM model, Integer userId) {
        Date now = new Date();
        Integer gradeLevel = subjectService.levelBySubjectId(model.getSubjectId());

        //题干、解析、选项等 插入
        TextContent infoTextContent = new TextContent();
        infoTextContent.setCreateTime(now);
        setQuestionInfoFromVM(infoTextContent, model);
        textContentService.insertByFilter(infoTextContent);

        Integer maxNo = questionMapper.selectMaxNo();
        Integer questionNo = 1;
        if (null != maxNo) {
            questionNo = maxNo + 1;
        }

        Question question = new Question();
        question.setSubjectId(model.getSubjectId());
        question.setGradeLevel(gradeLevel);
        question.setCreateTime(now);
        question.setQuestionType(model.getQuestionType());
        question.setStatus(QuestionStatusEnum.OK.getCode());
        question.setCorrectFromVM(model.getCorrect(), model.getCorrectArray());
        question.setScore(ExamUtil.scoreFromVM(model.getScore()));
        question.setDifficult(model.getDifficult());
        question.setInfoTextContentId(infoTextContent.getId());
        question.setCreateUser(userId);
        question.setDeleted(false);
        question.setQuestionNo(questionNo);
        questionMapper.insertSelective(question);
        model.setId(question.getId());
        return question;
    }

    @Override
    @Transactional
    public Question updateFullQuestion(QuestionEditRequestVM model) {
        Integer gradeLevel = subjectService.levelBySubjectId(model.getSubjectId());
        Question question = questionMapper.selectByPrimaryKey(model.getId());
        question.setSubjectId(model.getSubjectId());
        question.setGradeLevel(gradeLevel);
        question.setScore(ExamUtil.scoreFromVM(model.getScore()));
        question.setDifficult(model.getDifficult());
        question.setCorrectFromVM(model.getCorrect(), model.getCorrectArray());
        questionMapper.updateByPrimaryKeySelective(question);

        //题干、解析、选项等 更新
        TextContent infoTextContent = textContentService.selectById(question.getInfoTextContentId());
        setQuestionInfoFromVM(infoTextContent, model);
        textContentService.updateByIdFilter(infoTextContent);

        return question;
    }

    @Override
    @Cacheable(value = "xzs:question", key = "#id", unless = "#result == null")
    public Question get(Integer id) {
        return questionMapper.selectByPrimaryKey(id);
    }

    @Override
    public QuestionEditRequestVM getQuestionEditRequestVM(Integer questionId) {
        //题目映射
        Question question = questionMapper.selectByPrimaryKey(questionId);
        return getQuestionEditRequestVM(question);
    }

    @Override
    public QuestionEditRequestVM getQuestionEditRequestVM(Question question) {
        //题目映射
        TextContent questionInfoTextContent = textContentService.selectById(question.getInfoTextContentId());
        QuestionObject questionObject = JsonUtil.toJsonObject(questionInfoTextContent.getContent(), QuestionObject.class);
        QuestionEditRequestVM questionEditRequestVM = modelMapper.map(question, QuestionEditRequestVM.class);
        questionEditRequestVM.setTitle(questionObject.getTitleContent());

        //答案
        QuestionTypeEnum questionTypeEnum = QuestionTypeEnum.fromCode(question.getQuestionType());
        switch (questionTypeEnum) {
            case SingleChoice:
            case TrueFalse:
                questionEditRequestVM.setCorrect(question.getCorrect());
                break;
            case MultipleChoice:
                questionEditRequestVM.setCorrectArray(ExamUtil.contentToArray(question.getCorrect()));
                break;
            case GapFilling:
                List<String> correctContent = questionObject.getQuestionItemObjects().stream().map(d -> d.getContent()).collect(Collectors.toList());
                questionEditRequestVM.setCorrectArray(correctContent);
                break;
            case ShortAnswer:
                questionEditRequestVM.setCorrect(questionObject.getCorrect());
                break;
            default:
                break;
        }
        questionEditRequestVM.setScore(ExamUtil.scoreToVM(question.getScore()));
        questionEditRequestVM.setAnalyze(questionObject.getAnalyze());


        //题目项映射
        List<QuestionEditItemVM> editItems = questionObject.getQuestionItemObjects().stream().map(o -> {
            QuestionEditItemVM questionEditItemVM = modelMapper.map(o, QuestionEditItemVM.class);
            if (o.getScore() != null) {
                questionEditItemVM.setScore(ExamUtil.scoreToVM(o.getScore()));
            }
            return questionEditItemVM;
        }).collect(Collectors.toList());
        questionEditRequestVM.setItems(editItems);
        questionEditRequestVM.setQuestionTypeName(QuestionTypeEnum.fromCode(questionEditRequestVM.getQuestionType()).getName());
        return questionEditRequestVM;
    }

    @Override
    public QuestionEditRequestVM getQuestionEditRequestVM(Integer gradeLevel, Integer subjectId, Integer preId) {
        QuestionListRequestVM condition = new QuestionListRequestVM();
        condition.setLevel(gradeLevel);
        condition.setSubjectId(subjectId);
        condition.setPreId(preId);
        condition.setOrderBy("id", "asc");
        condition.setLimit(1);
        List<Question> questionList = questionMapper.selectByCondition(condition);
        if (CollectionUtils.isEmpty(questionList)) {
            return null;
        }
        return getQuestionEditRequestVM(questionList.get(0));
    }

    public void setQuestionInfoFromVM(TextContent infoTextContent, QuestionEditRequestVM model) {
        List<QuestionItemObject> itemObjects = model.getItems().stream().map(i ->
                {
                    QuestionItemObject item = new QuestionItemObject();
                    item.setPrefix(i.getPrefix());
                    item.setContent(i.getContent());
                    item.setItemUuid(i.getItemUuid());
                    item.setScore(ExamUtil.scoreFromVM(i.getScore()));
                    return item;
                }
        ).collect(Collectors.toList());
        QuestionObject questionObject = new QuestionObject();
        questionObject.setQuestionItemObjects(itemObjects);
        questionObject.setAnalyze(model.getAnalyze());
        questionObject.setTitleContent(model.getTitle());
        questionObject.setCorrect(model.getCorrect());
        infoTextContent.setContent(JsonUtil.toJsonStr(questionObject));
    }

    @Override
    public Integer selectAllCount() {
        return questionMapper.selectAllCount();
    }

    @Override
    public Integer selectCount(QuestionPageRequestVM requestVM) {
        return questionMapper.selectCount(requestVM);
    }

    @Override
    public List<Integer> selectMothCount() {
        Date startTime = DateTimeUtil.getMonthStartDay();
        Date endTime = DateTimeUtil.getMonthEndDay();
        List<String> mothStartToNowFormat = DateTimeUtil.MothStartToNowFormat();
        List<KeyValue> mouthCount = questionMapper.selectCountByDate(startTime, endTime);
        return mothStartToNowFormat.stream().map(md -> {
            KeyValue keyValue = mouthCount.stream().filter(kv -> kv.getName().equals(md)).findAny().orElse(null);
            return null == keyValue ? 0 : keyValue.getValue();
        }).collect(Collectors.toList());
    }

    @Override
    public void batchDelete(Integer[] ids) {
        questionMapper.deleteByIds(ids);
    }

    @Override
    public void readExcel(MultipartFile multipartFile,Integer level,Integer subjectId, Integer userId) {
        if(ObjectUtil.isNull(level) || ObjectUtil.isNull(subjectId)){
            throw new BadRequestException("导入失败！参数校验不通过");
        }
        FileUtil.checkSize(properties.getMaxSize(), multipartFile.getSize());
        String suffix = FileUtil.getExtensionName(multipartFile.getOriginalFilename());
        String type = FileUtil.getFileType(suffix);
        File file = FileUtil.upload(multipartFile, properties.getPath().getPath() + type +  File.separator);
        if(ObjectUtil.isNull(file)){
            throw new BadRequestException("上传失败");
        }
        List<QuestionEditRequestVM> questions = readDataFromExcel(file, level, subjectId);
        for (QuestionEditRequestVM question : questions) {
            this.insertFullQuestion(question, userId);
            addEsData(question);
        }
    }

    @Override
    public void initEsIndex() throws IOException {
        List<Question> list = questionMapper.selectByCondition(new QuestionListRequestVM());
        List<QuestionEditRequestVM> requestVMList = list.stream().map(x->getQuestionEditRequestVM(x)).collect(Collectors.toList());
        ElasticsearchUtil.deleteIndex("exam-system");
        if (!ElasticsearchUtil.createIkMapping("exam-system","question","title")) {
            throw new BusinessException("创建映射失败！");
        }
        requestVMList.stream().forEach(x->addEsData(x));
    }

    @Override
    public EsPage pageForEs(QuestionPageRequestEsVM model) {
        if (StringUtils.isEmpty(model.getTitle())) {
            return new EsPage(0, 10, 0, new ArrayList<>());
        }
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("title", model.getTitle());
        EsPage result = ElasticsearchUtil.searchDataPage("exam-system", "question",model.getPageIndex(),model.getPageSize(), queryBuilder, null, null, "title");
        return result;
    }

    @Override
    public void delByCondition(Integer level, Integer subjectId) {
        QuestionListRequestVM requestVM = new QuestionListRequestVM();
        requestVM.setSubjectId(subjectId);
        List<Question> list = questionMapper.selectByCondition(requestVM);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.stream().forEach(x->ElasticsearchUtil.deleteDataById("exam-system", "question", x.getId().toString()));
        questionMapper.delBySubjectId(subjectId);
    }

    private List<QuestionEditRequestVM> readDataFromExcel(File file,Integer gradeLevel,Integer subjectId) {
        List<QuestionEditRequestVM> questions = new ArrayList<>();
        if (null == file) {
            return questions;
        }
        ExcelReader reader = new ExcelReader(file, 0);
        List<Map<String,Object>> readAll = reader.readAll();
        if (CollectionUtils.isEmpty(readAll)){
            throw new BadRequestException("导入失败！Excel模版有误");
        }
        if (!readAll.get(0).containsKey("题干")||!readAll.get(0).containsKey("题型")||!readAll.get(0).containsKey("答案")
            ||!readAll.get(0).containsKey("选项A")||!readAll.get(0).containsKey("选项B")) {
            throw new BadRequestException("导入失败！Excel模版有误");
        }
        for (Map<String, Object> stringObjectMap : readAll) {
            QuestionEditRequestVM question = new QuestionEditRequestVM();
            question.setTitle(null != stringObjectMap.get("题干")?stringObjectMap.get("题干").toString():null);
            question.setQuestionType("选择题".equals(stringObjectMap.get("题型"))?QuestionTypeEnum.SingleChoice.getCode():QuestionTypeEnum.TrueFalse.getCode());
            question.setCorrect(null != stringObjectMap.get("答案")?stringObjectMap.get("答案").toString():null);
            question.setAnalyze("无");
            question.setDifficult(1);
            question.setScore("1");
            question.setGradeLevel(gradeLevel);
            question.setSubjectId(subjectId);
            List<QuestionEditItemVM> items = new ArrayList<>();
            QuestionEditItemVM item;
            if (null != stringObjectMap.get("选项A") && StringUtils.isNotEmpty(stringObjectMap.get("选项A").toString())) {
                item = new QuestionEditItemVM();
                item.setContent(stringObjectMap.get("选项A").toString());
                item.setPrefix("A");
                items.add(item);
            }
            if (null != stringObjectMap.get("选项B") && StringUtils.isNotEmpty(stringObjectMap.get("选项B").toString())) {
                item = new QuestionEditItemVM();
                item.setContent(stringObjectMap.get("选项B").toString());
                item.setPrefix("B");
                items.add(item);
            }
            if (null != stringObjectMap.get("选项C") && StringUtils.isNotEmpty(stringObjectMap.get("选项C").toString())) {
                item = new QuestionEditItemVM();
                item.setContent(stringObjectMap.get("选项C").toString());
                item.setPrefix("C");
                items.add(item);
            }
            if (null != stringObjectMap.get("选项D") && StringUtils.isNotEmpty(stringObjectMap.get("选项D").toString())) {
                item = new QuestionEditItemVM();
                item.setContent(stringObjectMap.get("选项D").toString());
                item.setPrefix("D");
                items.add(item);
            }
            question.setItems(items);
            questions.add(question);
        }
        return questions;
    }

    @Override
    public void addEsData(QuestionEditRequestVM requestVM) {
        addEsData(requestVM, requestVM.getId().toString());
    }

    @Override
    public void updateEsData(QuestionEditRequestVM model) {
        ElasticsearchUtil.deleteDataById("exam-system","question",model.getId().toString());
    }

    private void addEsData(QuestionEditRequestVM requestVM, String id) {
        QuestionEsObject esObject = modelMapper.map(requestVM, QuestionEsObject.class);
        List<QuestionEsItemObject> esItems = requestVM.getItems().stream().map(x -> modelMapper.map(x, QuestionEsItemObject.class)).collect(Collectors.toList());
        esObject.setItems(esItems);
        ElasticsearchUtil.addData(JSONObject.parseObject(JSONObject.toJSONString(esObject)), "exam-system", "question", id);
    }

}
