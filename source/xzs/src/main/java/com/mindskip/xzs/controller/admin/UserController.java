package com.mindskip.xzs.controller.admin;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.google.common.collect.Lists;
import com.mindskip.xzs.base.BaseApiController;
import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.configuration.property.FileProperties;
import com.mindskip.xzs.domain.Subject;
import com.mindskip.xzs.domain.UserSubject;
import com.mindskip.xzs.domain.other.KeyValue;
import com.mindskip.xzs.domain.User;
import com.mindskip.xzs.domain.UserEventLog;
import com.mindskip.xzs.domain.enums.UserStatusEnum;
import com.mindskip.xzs.exception.BadRequestException;
import com.mindskip.xzs.repository.SubjectMapper;
import com.mindskip.xzs.repository.UserSubjectMapper;
import com.mindskip.xzs.service.AuthenticationService;
import com.mindskip.xzs.service.UserEventLogService;
import com.mindskip.xzs.service.UserService;
import com.mindskip.xzs.utility.DateTimeUtil;
import com.mindskip.xzs.utility.FileUtil;
import com.mindskip.xzs.viewmodel.admin.user.*;
import com.mindskip.xzs.utility.PageInfoHelper;
import com.github.pagehelper.PageInfo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author 武汉思维跳跃科技有限公司
 */
@RestController("AdminUserController")
@RequestMapping(value = "/api/admin/user")
public class UserController extends BaseApiController {

    private final UserService userService;
    private final UserEventLogService userEventLogService;
    private final AuthenticationService authenticationService;
    private final FileProperties properties;
    private final UserSubjectMapper userSubjectMapper;

    private final SubjectMapper subjectMapper;

    @Autowired
    public UserController(UserService userService, UserEventLogService userEventLogService, AuthenticationService authenticationService, FileProperties properties, UserSubjectMapper userSubjectMapper, SubjectMapper subjectMapper) {
        this.userService = userService;
        this.userEventLogService = userEventLogService;
        this.authenticationService = authenticationService;
        this.properties = properties;
        this.userSubjectMapper = userSubjectMapper;
        this.subjectMapper = subjectMapper;
    }


    @RequestMapping(value = "/page/list", method = RequestMethod.POST)
    public RestResponse<PageInfo<UserResponseVM>> pageList(@RequestBody UserPageRequestVM model) {
        PageInfo<User> pageInfo = userService.userPage(model);
        PageInfo<UserResponseVM> page = PageInfoHelper.copyMap(pageInfo, d -> {
            UserResponseVM vm = UserResponseVM.from(d);
            List<UserSubject> list = userSubjectMapper.findByUserId(d.getId());
            vm.setSubjectIds(list.stream().map(x->x.getSubjectId()).collect(Collectors.toList()));
            vm.setLevelSubjectIds(list.stream().map(x->String.valueOf(x.getLevelId())+'-'+x.getSubjectId()).collect(Collectors.toList()));
            return vm;
        });
        return RestResponse.ok(page);
    }


    @RequestMapping(value = "/event/page/list", method = RequestMethod.POST)
    public RestResponse<PageInfo<UserEventLogVM>> eventPageList(@RequestBody UserEventPageRequestVM model) {
        PageInfo<UserEventLog> pageInfo = userEventLogService.page(model);
        PageInfo<UserEventLogVM> page = PageInfoHelper.copyMap(pageInfo, d -> {
            UserEventLogVM vm = modelMapper.map(d, UserEventLogVM.class);
            vm.setCreateTime(DateTimeUtil.dateFormat(d.getCreateTime()));
            return vm;
        });
        return RestResponse.ok(page);
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public RestResponse<List<UserResponseVM>> pageList(@RequestBody UserRequestVM requestVM) {
        requestVM.setRole(1);
        Integer subjectId = requestVM.getSubjectId();
        if (null == subjectId || subjectId <= 0) {
            return RestResponse.ok(Lists.newArrayList());
        }
        List<User> list = userService.userList(requestVM);
        UserSubject condition = new UserSubject();
        condition.setSubjectId(subjectId);
        List<UserSubject> userSubjects = userSubjectMapper.findByCondition(condition);
        list = list.stream().filter(x -> userSubjects.stream().anyMatch(y -> x.getId().equals(y.getUserId()))).collect(Collectors.toList());
        List<UserResponseVM> result = CollectionUtils.isEmpty(list) ? new ArrayList<>() : list.stream().map(x -> UserResponseVM.from(x)).collect(Collectors.toList());
        return RestResponse.ok(result);
    }

    @RequestMapping(value = "/select/{id}", method = RequestMethod.POST)
    public RestResponse<UserResponseVM> select(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        UserResponseVM userVm = UserResponseVM.from(user);
        return RestResponse.ok(userVm);
    }

    @RequestMapping(value = "/current", method = RequestMethod.POST)
    public RestResponse<UserResponseVM> current() {
        User user = getCurrentUser();
        UserResponseVM userVm = UserResponseVM.from(user);
        return RestResponse.ok(userVm);
    }

    @RequestMapping(value = "/updateSubject", method = RequestMethod.POST)
    public RestResponse<User> updateSubject(@RequestBody UserSubjectUpdateVM vm) {
        userService.updateSubject(vm.getUserId(), vm.getSubjectIds());
        return RestResponse.ok();
    }


    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public RestResponse<User> edit(@RequestBody @Valid UserCreateVM model) {
        if (model.getId() == null) {  //create
            User existUser = userService.getUserByUserName(model.getUserName());
            if (null != existUser) {
                return new RestResponse<>(2, "用户已存在");
            }

            if (StringUtils.isBlank(model.getPassword())) {
                return new RestResponse<>(3, "密码不能为空");
            }
        }
        if (StringUtils.isBlank(model.getBirthDay())) {
            model.setBirthDay(null);
        }
        User user = modelMapper.map(model, User.class);

        if (model.getId() == null) {
            String encodePwd = authenticationService.pwdEncode(model.getPassword());
            user.setPassword(encodePwd);
            user.setUserUuid(UUID.randomUUID().toString());
            user.setCreateTime(new Date());
            user.setLastActiveTime(new Date());
            user.setDeleted(false);
            userService.insertByFilter(user);
        } else {
            if (!StringUtils.isBlank(model.getPassword())) {
                String encodePwd = authenticationService.pwdEncode(model.getPassword());
                user.setPassword(encodePwd);
            }
            user.setModifyTime(new Date());
            userService.updateByIdFilter(user);
        }
        return RestResponse.ok(user);
    }


    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public RestResponse update(@RequestBody @Valid UserUpdateVM model) {
        User user = userService.selectById(getCurrentUser().getId());
        modelMapper.map(model, user);
        user.setModifyTime(new Date());
        userService.updateByIdFilter(user);
        return RestResponse.ok();
    }


    @RequestMapping(value = "/changeStatus/{id}", method = RequestMethod.POST)
    public RestResponse<Integer> changeStatus(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        UserStatusEnum userStatusEnum = UserStatusEnum.fromCode(user.getStatus());
        Integer newStatus = userStatusEnum == UserStatusEnum.Enable ? UserStatusEnum.Disable.getCode() : UserStatusEnum.Enable.getCode();
        user.setStatus(newStatus);
        user.setModifyTime(new Date());
        userService.updateByIdFilter(user);
        return RestResponse.ok(newStatus);
    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public RestResponse delete(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        user.setDeleted(true);
        userService.updateByIdFilter(user);
        return RestResponse.ok();
    }

    @RequestMapping(value = "/batchDelete", method = RequestMethod.POST)
    public RestResponse delete(@RequestBody Integer[] ids) {
        userService.deleteByIdsLogic(ids);
        return RestResponse.ok();
    }


    @RequestMapping(value = "/selectByUserName", method = RequestMethod.POST)
    public RestResponse<List<KeyValue>> selectByUserName(@RequestBody String userName) {
        List<KeyValue> keyValues = userService.selectByUserName(userName);
        return RestResponse.ok(keyValues);
    }

    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public RestResponse importExcel(@RequestParam("level") Integer level,@RequestParam("file") MultipartFile file) {
        try {
            readExcel(file, level, getCurrentUser().getId());
        } catch (BadRequestException bad) {
            return new RestResponse<>(bad.getStatus(), bad.getMessage());
        }
        return RestResponse.ok();
    }

    public void readExcel(MultipartFile multipartFile, Integer level, Integer userId) {
        if(ObjectUtil.isNull(level)){
            throw new BadRequestException("导入失败！参数校验不通过");
        }
        FileUtil.checkSize(properties.getMaxSize(), multipartFile.getSize());
        String suffix = FileUtil.getExtensionName(multipartFile.getOriginalFilename());
        String type = FileUtil.getFileType(suffix);
        File file = FileUtil.upload(multipartFile, properties.getPath().getPath() + type +  File.separator);
        if(ObjectUtil.isNull(file)){
            throw new BadRequestException("上传失败");
        }
        List<User> users = readDataFromExcel(file, level);
        userService.insertUsersByExcel(users);
    }

    private List<User> readDataFromExcel(File file,Integer gradeLevel) {
        List<User> users = new ArrayList<>();
        if (null == file) {
            return users;
        }
        ExcelReader reader = new ExcelReader(file, 0);
        List<Map<String,Object>> readAll = reader.readAll();
        if (CollectionUtils.isEmpty(readAll)){
            throw new BadRequestException("导入失败！Excel模版有误");
        }
        if (!readAll.get(0).containsKey("登录名")||!readAll.get(0).containsKey("密码")||!readAll.get(0).containsKey("姓名")
                ||!readAll.get(0).containsKey("手机号")) {
            throw new BadRequestException("导入失败！Excel模版有误");
        }
        Date now = new Date();
        for (Map<String, Object> stringObjectMap : readAll) {
            User user = new User();
            user.setDeleted(false);
            user.setStatus(1);
            user.setUserLevel(gradeLevel);
            user.setUserName(stringObjectMap.get("登录名").toString());
            user.setPassword(stringObjectMap.get("密码").toString());
            user.setRealName(stringObjectMap.get("姓名").toString());
            user.setPhone(stringObjectMap.get("手机号").toString());
            String projectNames = (null == stringObjectMap.get("准操项目")) ? null : stringObjectMap.get("准操项目").toString();
            if (!StringUtils.isEmpty(projectNames)) {
                String[] projectNameArr = projectNames.split(",");
                List<Subject> subjectList = Arrays.stream(projectNameArr).distinct().map(x->subjectMapper.getSubjectByName(x)).collect(Collectors.toList());
                user.setSubjectList(subjectList);
            }
            user.setRole(1);
            String encodePwd = authenticationService.pwdEncode(user.getPassword());
            user.setPassword(encodePwd);
            user.setCreateTime(now);
            user.setLastActiveTime(now);
            user.setUserUuid(UUID.randomUUID().toString());
            users.add(user);
        }
        return users;
    }
}
