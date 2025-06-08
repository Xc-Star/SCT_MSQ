package com.sct.test;

import com.sct.entity.AdminUser;
import com.sct.mapper.AdminUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @Title: Test
 * @Author Xc_Star
 * @Package com.sct.test
 * @Date 2025/6/9 00:45
 */

@SpringBootTest
public class AdminUserMapperTest {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Test
    void testBuiltInMethods() {
        // 测试内置方法是否存在
        assertNotNull(adminUserMapper.selectById(1L));
        assertNotNull(adminUserMapper.updateById(new AdminUser()));

    }

}
