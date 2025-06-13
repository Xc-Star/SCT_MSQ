package com.sct.service;

import com.sct.dto.MemberPageQueryDTO;
import com.sct.dto.MemberRemoveDTO;
import com.sct.entity.AdminUser;
import com.sct.result.PageResult;
import com.sct.vo.MemberListVO;

/**
 * @Title: MemberService
 * @Author Xc_Star
 * @Package com.sct.service
 * @Date 2025/6/13 01:59
 */
public interface MemberService {

    /**
     * 获取服务器成员列表分页
     * @param pageParam
     * @return
     */
    PageResult<MemberListVO> pageQuery(MemberPageQueryDTO pageParam);

    /**
     * 更新管理员信息
     * @param adminUser 管理员
     */
    void updateAdminUser(AdminUser adminUser);

    /**
     * 移除服务器成员
     * @param memberRemoveDTO 移除信息
     */
    void remove(MemberRemoveDTO memberRemoveDTO);
}
