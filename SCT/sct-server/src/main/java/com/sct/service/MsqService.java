package com.sct.service;

import com.sct.entity.Msq;

import java.util.List;

/**
 * @Title: MsqService
 * @Author Xc_Star
 * @Package com.sct.service.impl
 * @Date 2025/6/6 22:31
 */
public interface MsqService {

    /**
     * 获取问卷列表
     * @return 问卷列表
     */
    List<Msq> list();
}
