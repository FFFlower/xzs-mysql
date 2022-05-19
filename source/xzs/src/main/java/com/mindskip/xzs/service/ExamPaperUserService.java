package com.mindskip.xzs.service;

import com.mindskip.xzs.domain.ExamPaperUser;

import java.util.List;

/**
 * <p><一句话功能简述>
 * <p><功能详细描述>
 * <p>Copyright (c) 2022/3/21, wangyu@zhuofansoft.com All Rights Reserve</p>
 *
 * @author wangyu
 * @version [V1.0, 2022/3/21]
 * @see [相关类/方法]
 */
public interface ExamPaperUserService {

    List<ExamPaperUser> findByUserId(Integer userId);

    List<ExamPaperUser> findByPaperId(Integer paperId);

    int deleteByPaperId(Integer paperId);

    void insertBatch(List<ExamPaperUser> list);

    List<ExamPaperUser> findByPaperIds(List<Integer> examPaperIds);
}
