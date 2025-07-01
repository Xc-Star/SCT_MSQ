package com.sct.service.impl;

import com.sct.context.BaseContext;
import com.sct.entity.Config;
import com.sct.mapper.ConfigMapper;
import com.sct.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: ConfigServiceImpl
 * @Author Xc_Star
 * @Package com.sct.service.impl
 * @Date 2025/6/30 03:24
 */

@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ConfigMapper configMapper;

    @Override
    public List<Config> list() {
        return configMapper.selectList(null);
    }

    @Override
    public void update(Config config) {
        String username = BaseContext.getUsername();
        config.setRemark(username);
        config.setUpdateTime(LocalDateTime.now());
        configMapper.updateById(config);
    }
}
