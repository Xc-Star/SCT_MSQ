package com.sct.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sct.entity.TopicResult;
import com.sct.vo.MsqReviewInfoVO;
import com.sct.vo.TopicResultVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Title: TopicResultMapper
 * @Author Xc_Star
 * @Package com.sct.mapper
 * @Date 2025/6/6 22:34
 */

@Mapper
public interface TopicResultMapper extends BaseMapper<TopicResult> {

    void saveBatch(List<TopicResult> topicResults);

    List<TopicResultVO> getTopicResultsByMsqResultId(Long msqResultId);
}
