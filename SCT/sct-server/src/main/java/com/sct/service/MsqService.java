package com.sct.service;

import com.sct.dto.MsqResultPageDTO;
import com.sct.dto.MsqResultUpdateStatusDTO;
import com.sct.entity.Msq;
import com.sct.entity.MsqResult;
import com.sct.result.PageResult;

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

    /**
     * 根据类型获取问卷
     * @param type 类型
     * @return 问卷列表
     */
    List<Msq> getTypeMsq(Integer type);

    /**
     * 获取问卷结果列表
     * @param msqResultPageDTO 分页参数
     * @return 问卷结果列表
     */
    PageResult<MsqResult> getResultPage(MsqResultPageDTO msqResultPageDTO);

    /**
     * 修改问卷结果状态
     * @param msqResultUpdateStatusDTO 修改参数
     */
    void updateMsqResultStatus(MsqResultUpdateStatusDTO msqResultUpdateStatusDTO);
}
