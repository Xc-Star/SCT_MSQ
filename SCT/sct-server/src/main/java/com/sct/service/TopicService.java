package com.sct.service;

import com.sct.dto.MsqSubmitDTO;
import com.sct.dto.MsqUpdateDTO;
import com.sct.entity.Msq;
import com.sct.vo.MsqInfoVO;
import com.sct.vo.MsqReviewInfoVO;

import java.util.List;

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
    MsqInfoVO getOneMsqInfoVO(Long msqId);

    /**
     * 获取问卷
     * @param type 问卷类型
     * @return 问卷
     */
    List<Msq> getMsqInfoVO(String type);

    /**
     * 修改问卷
     * @param msqUpdateDTO 修改问卷参数
     */
    void updateById(MsqUpdateDTO msqUpdateDTO);

    /**
     * 提交问卷
     * @param msqSubmitDTO 提交问卷参数
     */
    void submit(MsqSubmitDTO msqSubmitDTO);

    /**
     * 获取问卷结果
     * @param msqResultId 问卷结果id
     * @return 问卷结果
     */
    MsqReviewInfoVO getReviewInfoById(Long msqResultId);
}
