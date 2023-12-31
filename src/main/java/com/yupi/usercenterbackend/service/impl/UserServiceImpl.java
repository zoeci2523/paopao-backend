package com.yupi.usercenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yupi.usercenterbackend.common.ErrorCode;
import com.yupi.usercenterbackend.exception.BusinessException;
import com.yupi.usercenterbackend.model.User;
import com.yupi.usercenterbackend.service.UserService;
import com.yupi.usercenterbackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.yupi.usercenterbackend.constants.UserConstants.ADMIN_ROLE;
import static com.yupi.usercenterbackend.constants.UserConstants.USER_LGGIN_STATE;

/**
* @author fengxiaoha
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-09-10 11:56:33
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    private static final String SALT = "aisodng92r--#";


    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册
     * @param account
     * @param password
     * @param checkPassword
     * @return
     */
    @Override
    public long register(String account, String password, String checkPassword, String planetCode){
        //1 校验
        if (StringUtils.isAnyBlank(account, password, checkPassword, planetCode)){
            throw new BusinessException(ErrorCode.NULL_ERROR,"参数为空");
        }
        if (account.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度不得小于4位");
        }
        if (password.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码不得小于8位");
        }
        if (planetCode.length() > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号过长");
        }
        // 账号不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(account);
        if (matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号名不能包含特殊字符");
        }
        // 密码和检验密码相同
        if (!password.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码与校验密码不一致");
        }

        // 用户不能重复
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", account);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.ACCOUNT_REPEATED,"不能重复注册");
        }

        // 星球编号不能重复
        queryWrapper = new QueryWrapper();
        queryWrapper.eq("planetCode", planetCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.ACCOUNT_REPEATED,"星球账号不能重复注册");
        }
        // 加盐
        String encryptPW = DigestUtils.md5DigestAsHex((SALT+password).getBytes(StandardCharsets.UTF_8));
        // 插入数据
        User user = new User();
        user.setUserAccount(account);
        user.setUserPassword(encryptPW);
        user.setPlanetCode(planetCode);
        boolean result = save(user);
        // 获得的是Long，为了防止拆箱错误，最好判断下获取结果
        if (!result){
            throw new BusinessException(ErrorCode.NOT_EXIST,"用户不存在");
        }
        return user.getId();
    }

    @Override
    public User login(String account, String password, HttpServletRequest request){
        //1 校验
        if (StringUtils.isAnyBlank(account, password)){
            throw new BusinessException(ErrorCode.NULL_ERROR,"参数为空");
        }
        if (account.length() < 6){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号名长度不得小于6位");
        }
        if (password.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度不得小于8位");
        }

        // 加盐
        String encryptPW = DigestUtils.md5DigestAsHex((SALT+password).getBytes(StandardCharsets.UTF_8));
        // 查询数据
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", account);
        queryWrapper.eq("userPassword", encryptPW);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null){
            log.info("User login failed: account did not match with password");
            throw new BusinessException(ErrorCode.NOT_EXIST,"用户不存在");
        }
        // 用户脱敏
        User cleanUser = getCleanUser(user);

        // 记录用户登录态
        request.getSession().setAttribute(USER_LGGIN_STATE, cleanUser);
        return cleanUser;
    }

    /**
     * 用户脱敏逻辑
     * @param user
     * @return
     */
    @Override
    public User getCleanUser(User user) {
        if (user == null) throw new BusinessException(ErrorCode.NOT_EXIST,"用户不存在");;
        User cleanUser = new User();
        cleanUser.setId(user.getId());
        cleanUser.setUsername(user.getUsername());
        cleanUser.setUserAccount(user.getUserAccount());
        cleanUser.setAvatarUrl(user.getAvatarUrl());
        cleanUser.setGender(user.getGender());
        cleanUser.setPhone(user.getPhone());
        cleanUser.setEmail(user.getEmail());
        cleanUser.setProfile(user.getProfile());
        cleanUser.setPlanetCode(user.getPlanetCode());
        cleanUser.setUserRole(user.getUserRole());
        cleanUser.setUserStatus(user.getUserStatus());
        cleanUser.setCreateTime(user.getCreateTime());
        cleanUser.setTags(user.getTags());
        return cleanUser;
    }

    /**
     * 登出
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request){
        request.getSession().removeAttribute(USER_LGGIN_STATE);
        return 1;
    }

    /**
     * 根据标签搜索用户
     * @param tagNameList
     * @return
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList){
        // 判空
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 方法1：SQL查询，拼接 and 查询
        QueryWrapper<User> queryWrapper = new QueryWrapper();
//        for (String tagName: tagNameList) {
//            queryWrapper = queryWrapper.like("tags",tagName);
//        }
//        List<User> userList = userMapper.selectList(queryWrapper);
//        return userList.stream().map(this::getCleanUser).collect(Collectors.toList());

        // 方法2：内存查询
        // 2.1 先查询出所有用户
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 2.2 在内存中根据标签筛选用户
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            // 小技巧：gson list 转换使用 TypeToken
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>(){}.getType());
            // 需要判空，因为用户不一定有tags
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) return false;
            }
            return true;
        }).map(this:: getCleanUser).collect(Collectors.toList());
    }

    @Override
    public int updateUser(User user, User loginUser){
        Long userId = user.getId();
        if (userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 补充校验其他信息是否为空，如果用户仅传递id不传其他信息，直接报错，不需要执行update语句
        // 将允许修改的字段全部拿出来，判断是否全为空
        String userName = user.getUsername();
        String avatarUrl = user.getAvatarUrl();
        Integer gender = user.getGender();
        String phone = user.getPhone();
        String email = user.getEmail();
        if (StringUtils.isAllEmpty(userName, avatarUrl, phone, email) && gender == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 如果是管理员，允许更新任意用户
        // 如果不是管理员，仅允许更新自己信息
        if (!isAdmin(loginUser) && userId != loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        User oldUser = userMapper.selectById(userId);
        if (oldUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request){
        if (request == null){
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LGGIN_STATE);
        if (userObj == null){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) userObj;
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LGGIN_STATE);
        User user = (User) userObj;
        if (user == null || user.getUserRole() != ADMIN_ROLE) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser == null || loginUser.getUserRole() != ADMIN_ROLE;
    }
}




