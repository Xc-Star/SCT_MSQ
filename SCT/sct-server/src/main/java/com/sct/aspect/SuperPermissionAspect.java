package com.sct.aspect;

import com.sct.annotation.SuperPermission;
import com.sct.context.BaseContext;
import com.sct.exception.BaseException;
import com.sct.service.AdminUserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Title: SuperPermissionAspect
 * @Author Xc_Star
 * @Package com.sct.aspect
 * @Date 2025/7/16 14:18
 */

@Aspect
@Component
@Slf4j
public class SuperPermissionAspect {

    @Resource
    private AdminUserService adminUserService;

    @Before("@annotation(superPermission)")
    public void before(JoinPoint joinPoint, SuperPermission superPermission) {
        if (!adminUserService.isSuperAdmin(BaseContext.getCurrentId())) {
            throw new BaseException("权限不足");
        }
    }
}
