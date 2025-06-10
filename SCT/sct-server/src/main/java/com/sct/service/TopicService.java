package com.sct.service;

import com.sct.dto.MsqUpdateDTO;
import com.sct.vo.MsqInfoVO;

/**
 * @Title: TopicService
 * @Author Xc_Star
 * @Package com.sct.service
 * @Date 2025/6/9 15:34
 */
public interface TopicService {

    /**
     * 获取问卷明细
     * @param msqId 问卷id
     * @return 问卷明细
     */
    MsqInfoVO getMsqInfoVO(Long msqId);

    /**
     * 修改问卷
     * @param msqUpdateDTO 修改问卷参数
     */
    void updateById(MsqUpdateDTO msqUpdateDTO);
}
