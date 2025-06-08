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

    /**
     * 保存问卷
     * @param msq 问卷
     */
    void saveMsq(Msq msq);

    /**
     * 修改问卷
     * @param msq 问卷
     */
    void updateMsq(Msq msq);

    /**
     * 修改问卷状态
     * @param id 问卷id
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据id获取问卷
     * @param id 问卷id
     * @return 问卷
     */
    Msq getById(Long id);

    /**
     * 根据id删除问卷
     * @param id 问卷id
     */
    void deleteById(Long id);
}
