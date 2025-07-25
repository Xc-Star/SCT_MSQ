package com.sct.service.impl;

import com.sct.constant.JwtClaimsConstant;
import com.sct.constant.MessageConstant;
import com.sct.context.BaseContext;
import com.sct.dto.AdminLoginDTO;
import com.sct.dto.UpdatePasswordDTO;
import com.sct.entity.AdminUser;
import com.sct.exception.BaseException;
import com.sct.mapper.AdminUserMapper;
import com.sct.properties.JwtProperties;
import com.sct.service.AuthService;
import com.sct.utils.JwtUtil;
import com.sct.utils.PasswordUtil;
import com.sct.vo.LoginVO;
import com.sct.vo.UserInfoVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title: AuthServiceImpl
 * @Author Xc_Star
 * @Package com.sct.service.impl
 * @Date 2025/6/8 03:30
 */

@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private AdminUserMapper adminUserMapper;
    @Resource
    private JwtProperties jwtProperties;

    @Override
    public LoginVO login(AdminLoginDTO adminLoginDTO) {

        AdminUser user = adminUserMapper.getUserByUsername(adminLoginDTO.getUsername());
        if (user == null) {
            throw new BaseException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        if (!PasswordUtil.matches(adminLoginDTO.getPassword(), user.getPassword())) {
            throw new BaseException(MessageConstant.PASSWORD_ERROR);
        }

        user.setLastLoginTime(LocalDateTime.now());
        adminUserMapper.updateById(user);

        Map<String, Object> map = new HashMap<>();
        map.put(JwtClaimsConstant.USER_ID, user.getId());
        map.put(JwtClaimsConstant.USERNAME, user.getUsername());

        return LoginVO.builder()
                .token(JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), map))
                .userInfo(UserInfoVO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .avatar(user.getAvatar())
                        .isSuperAdmin(user.getIsSuperAdmin())
                        .build())
                .build();
    }

    @Override
    public void changePassword(UpdatePasswordDTO updatePasswordDTO) {

        Long currentId = BaseContext.getCurrentId();
        AdminUser user = adminUserMapper.selectById(currentId);
        if (!PasswordUtil.matches(updatePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new BaseException("旧密码错误");
        }

        user.setPassword(PasswordUtil.encode(updatePasswordDTO.getNewPassword()));
        adminUserMapper.updateById(user);
    }
}
