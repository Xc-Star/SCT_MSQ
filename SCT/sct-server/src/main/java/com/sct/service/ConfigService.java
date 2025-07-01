package com.sct.service;

import com.sct.entity.Config;

import java.util.List;

/**
 * @Title: ConfigService
 * @Author Xc_Star
 * @Package com.sct.service
 * @Date 2025/6/30 03:24
 */
public interface ConfigService {

    /**
     * 获取所有配置
     * @return 配置列表
     */
    List<Config> list();

    /**
     * 更新配置
     * @param config 配置
     */
    void update(Config config);
}
