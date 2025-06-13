package com.sct.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sct.entity.AdminUser;
import com.sct.entity.common.PageParam;
import com.sct.mapper.AdminUserMapper;
import com.sct.result.PageResult;
import com.sct.service.AdminUserService;
import com.sct.service.MemberService;
import com.sct.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Title: AdminUserServiceImpl
 * @Author Xc_Star
 * @Package com.sct.service.impl
 * @Date 2025/6/13 00:44
 */

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final String BASE_AVATAR_URL = "https://mc-heads.net/avatar/";

    @Resource
    private AdminUserMapper adminUserMapper;
    @Resource
    private MemberService memberService;

    @Override
    public PageResult<AdminUser> pageQuery(PageParam pageParam) {

        PageHelper.startPage(pageParam.getPageNo(), pageParam.getPageSize());
        try {
            List<AdminUser> adminUsers = adminUserMapper.selectList(null);
            PageInfo<AdminUser> pageInfo = new PageInfo<>(adminUsers);
            return new PageResult<>(pageInfo.getList(), pageInfo.getTotal());
        } finally {
            PageHelper.clearPage();
        }
    }

    @Transactional
    @Override
    public void update(AdminUser adminUser) {

        if (adminUser.getPassword() != null) {
            adminUser.setPassword(PasswordUtil.encode(adminUser.getPassword()));
        }
        adminUser.setAvatar(BASE_AVATAR_URL + adminUser.getUsername());
        adminUserMapper.updateById(adminUser);
        memberService.updateAdminUser(adminUser);
    }

    @Override
    public void save(AdminUser adminUser) {
        adminUser.setPassword(PasswordUtil.encode(adminUser.getPassword()));
        adminUser.setAvatar(BASE_AVATAR_URL + adminUser.getUsername());
        adminUserMapper.insert(adminUser);
    }

    @Override
    public void delete(Long id) {
        adminUserMapper.deleteById(id);
    }
}
