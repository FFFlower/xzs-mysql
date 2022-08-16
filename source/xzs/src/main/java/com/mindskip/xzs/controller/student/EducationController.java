package com.mindskip.xzs.controller.student;


import com.mindskip.xzs.base.BaseApiController;
import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.domain.Subject;
import com.mindskip.xzs.domain.User;
import com.mindskip.xzs.domain.UserSubject;
import com.mindskip.xzs.service.SubjectService;
import com.mindskip.xzs.service.UserSubjectService;
import com.mindskip.xzs.viewmodel.student.education.SubjectEditRequestVM;
import com.mindskip.xzs.viewmodel.student.education.SubjectVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController("StudentEducationController")
@RequestMapping(value = "/api/student/education")
public class EducationController extends BaseApiController {

    private final SubjectService subjectService;

    private final UserSubjectService userSubjectService;

    @Autowired
    public EducationController(SubjectService subjectService,UserSubjectService userSubjectService) {
        this.subjectService = subjectService;
        this.userSubjectService = userSubjectService;
    }

    @RequestMapping(value = "/subject/list", method = RequestMethod.POST)
    public RestResponse<List<SubjectVM>> list() {
        User user = getCurrentUser();
        List<Subject> subjects = subjectService.getSubjectByLevel(user.getUserLevel());
        List<SubjectVM> subjectVMS = subjects.stream().map(d -> {
            SubjectVM subjectVM = modelMapper.map(d, SubjectVM.class);
            subjectVM.setId(String.valueOf(d.getId()));
            return subjectVM;
        }).collect(Collectors.toList());
        return RestResponse.ok(subjectVMS);
    }

    @RequestMapping(value = "/subject/all-list", method = RequestMethod.POST)
    public RestResponse<List<Subject>> allList() {
        List<Subject> subjects = subjectService.list();
        return RestResponse.ok(subjects);
    }

    @RequestMapping(value = "/subject/listByUser", method = RequestMethod.POST)
    public RestResponse<List<Subject>> listByUser() {
        User user = getCurrentUser();
        List<UserSubject> userSubjects = userSubjectService.findByUserId(user.getId());
        List<Integer> subjectIds = userSubjects.stream().map(x->x.getSubjectId()).collect(Collectors.toList());
        List<Subject> subjects = subjectService.selectByIds(subjectIds);
        return RestResponse.ok(subjects);
    }

    @RequestMapping(value = "/subject/select/{id}", method = RequestMethod.POST)
    public RestResponse<SubjectEditRequestVM> select(@PathVariable Integer id) {
        Subject subject = subjectService.selectById(id);
        SubjectEditRequestVM vm = modelMapper.map(subject, SubjectEditRequestVM.class);
        return RestResponse.ok(vm);
    }

}
