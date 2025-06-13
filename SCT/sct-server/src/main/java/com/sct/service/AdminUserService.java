package com.sct.service;

import com.sct.entity.AdminUser;
import com.sct.entity.common.PageParam;
import com.sct.result.PageResult;

/**
 * @Title: AdminUserService
 * @Author Xc_Star
 * @Package com.sct.service
 * @Date 2025/6/13 00:44
 */
public interface AdminUserService {

    /**
     * 分页查询管理员账号列表
     * @param pageParam 分页参数
     * @return 管理员账号列表
     */
    PageResult<AdminUser> pageQuery(PageParam pageParam);

    /**
     * 更新管理员账号信息
     * @param adminUser 管理员账号信息
     */
    void update(AdminUser adminUser);

    /**
     * 保存管理员账号信息
     * @param adminUser 管理员账号信息
     */
    void save(AdminUser adminUser);

    /**
     * 删除管理员账号信息
     * @param id 管理员账号ID
     */
    void delete(Long id);
}
