package com.sct.service.impl;

import com.sct.entity.Msq;
import com.sct.mapper.MsqMapper;
import com.sct.service.MsqService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Title: MsqServiceImpl
 * @Author Xc_Star
 * @Package com.sct.service.impl
 * @Date 2025/6/6 22:32
 */

@Service
public class MsqServiceImpl implements MsqService {

    @Resource
    private MsqMapper msqMapper;

    /**
     * 获取所有问卷
     * @return
     */
    public List<Msq> list() {
        return msqMapper.selectList(null);
    }
}
