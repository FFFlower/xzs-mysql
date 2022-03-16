package com.mindskip.xzs.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.mindskip.xzs.configuration.property.FileProperties;
import com.mindskip.xzs.domain.other.KeyValue;
import com.mindskip.xzs.exception.BadRequestException;
import com.mindskip.xzs.exception.BusinessException;
import com.mindskip.xzs.domain.User;
import com.mindskip.xzs.event.OnRegistrationCompleteEvent;
import com.mindskip.xzs.repository.UserMapper;
import com.mindskip.xzs.service.AuthenticationService;
import com.mindskip.xzs.service.UserService;
import com.mindskip.xzs.utility.FileUtil;
import com.mindskip.xzs.viewmodel.admin.user.UserPageRequestVM;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

/**
 * @author 武汉思维跳跃科技有限公司
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

    private final static String CACHE_NAME = "xzs:user";
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, ApplicationEventPublisher eventPublisher) {
        super(userMapper);
        this.userMapper = userMapper;
        this.eventPublisher = eventPublisher;
    }


    @Override
    public List<User> getUsers() {
        return userMapper.getAllUser();
    }

    @Override
    public User getUserById(Integer id) {
        return userMapper.getUserById(id);
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#username", unless = "#result == null")
    public User getUserByUserName(String username) {
        return userMapper.getUserByUserName(username);
    }

    @Override
    @CacheEvict(value = CACHE_NAME, key = "#record.userName")
    public int insertByFilter(User record) {
        return super.insertByFilter(record);
    }

    @Override
    @CacheEvict(value = CACHE_NAME, key = "#record.userName")
    public int updateByIdFilter(User record) {
        return super.updateByIdFilter(record);
    }

    @Override
    @CacheEvict(value = CACHE_NAME, key = "#record.userName")
    public int updateById(User record) {
        return super.updateById(record);
    }

    @Override
    public User getUserByUserNamePwd(String username, String pwd) {
        return userMapper.getUserByUserNamePwd(username, pwd);
    }

    @Override
    public User getUserByUuid(String uuid) {
        return userMapper.getUserByUuid(uuid);
    }

    @Override
    public List<User> userPageList(String name, Integer pageIndex, Integer pageSize) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("name", name);
        map.put("offset", ((int) pageIndex) * pageSize);
        map.put("limit", pageSize);
        return userMapper.userPageList(map);
    }

    @Override
    public Integer userPageCount(String name) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("name", name);
        return userMapper.userPageCount(map);
    }


    @Override
    public PageInfo<User> userPage(UserPageRequestVM requestVM) {
        return PageHelper.startPage(requestVM.getPageIndex(), requestVM.getPageSize(), "id desc").doSelectPageInfo(() ->
                userMapper.userPage(requestVM)
        );
    }


    @Override
    public void insertUser(User user) {
        userMapper.insertSelective(user);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUsers(List<User> users) {
        userMapper.insertUsers(users);
    }

    @Override
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    @Override
    public void updateUsersAge(Integer age, List<Integer> ids) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("idslist", ids);
        map.put("age", age);
        userMapper.updateUsersAge(map);
    }

    @Override
    public void deleteUserByIds(List<Integer> ids) {
        userMapper.deleteUsersByIds(ids);
    }

    @Override
    public Integer selectAllCount() {
        return userMapper.selectAllCount();
    }

    @Override
    public List<KeyValue> selectByUserName(String userName) {
        return userMapper.selectByUserName(userName);
    }

    @Override
    public List<User> selectByIds(List<Integer> ids) {
        return userMapper.selectByIds(ids);
    }

    @Override
    public User selectByWxOpenId(String wxOpenId) {
        return userMapper.selectByWxOpenId(wxOpenId);
    }

    @Override
    @CacheEvict(value = CACHE_NAME, key = "#user.userName")
    @Transactional
    public void changePicture(User user, String imagePath) {
        User changePictureUser = new User();
        changePictureUser.setId(user.getId());
        changePictureUser.setImagePath(imagePath);
        userMapper.updateByPrimaryKeySelective(changePictureUser);
    }

    @Override
    public void deleteByIdsLogic(Integer[] ids) {
        userMapper.deleteByIdsLogic(ids);
    }

    @Override
    public void insertUsersByExcel(List<User> users) {
        validateUserName(users);
        this.insertUsers(users);
    }

    private void validateUserName(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }
        Map<String, User> userMap = new HashMap<>();
        for (User user : userList) {
            if (userMap.containsKey(user.getUserName())) {
                throw new BadRequestException("登录名【"+ user.getUserName() +"】重复，请检查！");
            }
            userMap.put(user.getUserName(), user);
        }
        List<User> allUser = userMapper.getAllUser();
        if (!CollectionUtils.isEmpty(allUser)) {
            for (User user : userMapper.getAllUser()) {
                if (userMap.containsKey(user.getUserName())) {
                    throw new BadRequestException("登录名【"+ user.getUserName() +"】重复，请检查！");
                }
            }
        }
    }
}
