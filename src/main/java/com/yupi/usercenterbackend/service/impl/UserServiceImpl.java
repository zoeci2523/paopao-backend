package com.yupi.usercenterbackend.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.usercenterbackend.common.ErrorCode;
import com.yupi.usercenterbackend.exception.BusinessException;
import com.yupi.usercenterbackend.model.User;
import com.yupi.usercenterbackend.service.UserService;
import com.yupi.usercenterbackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        cleanUser.setPlanetCode(user.getPlanetCode());
        cleanUser.setUserRole(user.getUserRole());
        cleanUser.setUserStatus(user.getUserStatus());
        cleanUser.setCreateTime(user.getCreateTime());
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

}




