package com.sct.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sct.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Title: AdminUserMapper
 * @Author Xc_Star
 * @Package com.sct.mapper
 * @Date 2025/6/6 22:33
 */

@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {

    @Select("select * from admin_user where username = #{username} AND deleted = 0")
    AdminUser getUserByUsername(String username);
}
