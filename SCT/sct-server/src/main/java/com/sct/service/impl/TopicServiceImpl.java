package com.sct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sct.context.BaseContext;
import com.sct.dto.MsqSubmitDTO;
import com.sct.dto.MsqUpdateDTO;
import com.sct.entity.Msq;
import com.sct.entity.MsqResult;
import com.sct.entity.Topic;
import com.sct.entity.TopicResult;
import com.sct.exception.BaseException;
import com.sct.mapper.MsqMapper;
import com.sct.mapper.MsqResultMapper;
import com.sct.mapper.TopicMapper;
import com.sct.mapper.TopicResultMapper;
import com.sct.service.TopicService;
import com.sct.utils.BeanUtils;
import com.sct.vo.MsqInfoVO;
import com.sct.vo.MsqReviewInfoVO;
import com.sct.vo.TopicResultVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Title: TopicServiceImpl
 * @Author Xc_Star
 * @Package com.sct.service.impl
 * @Date 2025/6/9 15:34
 */

@Service
public class TopicServiceImpl implements TopicService {

    @Resource
    private TopicMapper topicMapper;
    @Resource
    private TopicResultMapper topicResultMapper;
    @Resource
    private MsqMapper msqMapper;
    @Resource
    private MsqResultMapper msqResultMapper;

    @Override
    public MsqInfoVO getOneMsqInfoVO(Long msqId) {
        List<Topic> topics = getTopicParent();
        Msq msq = msqMapper.selectById(msqId);
        if (msq == null) {
            throw new BaseException("暂未开放...");
        }
        MsqInfoVO msqInfoVO = MsqInfoVO.builder()
                .id(msq.getId())
                .name(msq.getName())
                .description(msq.getDescription())
                .type(msq.getType())
                .build();
        List<Topic> topicList = topicMapper.selectList(new QueryWrapper<Topic>().eq("msq_id", msqId).orderByAsc("no"));
        if (topicList != null && !topicList.isEmpty()) topics.addAll(topicList);
        msqInfoVO.setTopics(topics);
        return msqInfoVO;
    }

    @Override
    public List<Msq> getMsqInfoVO(String type) {
        List<Msq> msqList = msqMapper.selectList(new QueryWrapper<Msq>().eq("type", type).eq("status", 1));
        if (msqList == null || msqList.isEmpty()) {
            throw new BaseException("暂未开放...");
        }
        return msqList;
    }

    @Transactional
    @Override
    public void updateById(MsqUpdateDTO msqUpdateDTO) {
        Msq msq = BeanUtils.toBean(msqUpdateDTO, Msq.class);
        msq.setRemark(BaseContext.getUsername());
        msqMapper.updateById(msq);

        List<Topic> topics = msqUpdateDTO.getTopics();
        List<Long> ids = getIds(topics);

        for (Topic topic : topics) {
            if (topic.getId() == null) {
                topic.setMsqId(msq.getId());
                topic.setMsqName(msq.getName());
                topicMapper.insert(topic);
                continue;
            }
            if (topic.getId() < 0) {
                ids.remove(topic.getId());
                continue;
            }
            topic.setId(null);
            topicMapper.insert(topic);
        }
        if (!ids.isEmpty()) topicMapper.deleteBatchIds(ids);
    }

    @Transactional
    @Override
    public void submit(MsqSubmitDTO msqSubmitDTO) {
        List<TopicResult> topicResults = msqSubmitDTO.getTopicResults();
        String respondent = topicResults.get(0).getTopicResult();
        String respondentContact = topicResults.get(1).getTopicResult();
        LocalDateTime now = LocalDateTime.now();
        List<MsqResult> msqResults = msqResultMapper.selectList(
                new QueryWrapper<MsqResult>()
                        .eq("respondent", respondent)
                        .between("create_time", now.minusDays(30), now));
        if (msqResults != null && !msqResults.isEmpty()) {
            throw new BaseException("您已提交过问卷，请联系管理员审核！");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(msqSubmitDTO.getAvatar()).append(",").append(msqSubmitDTO.getUuid());
        MsqResult msqResult = MsqResult.builder()
                .msqId(msqSubmitDTO.getMsqId())
                .msqName(msqSubmitDTO.getName())
                .respondent(respondent)
                .respondentContact(respondentContact)
                .type(msqSubmitDTO.getType())
                .status(MsqResult.STATUS_WAITING)
                .build();
        msqResult.setRemark(sb.toString());
        msqResultMapper.insert(msqResult);
        topicResults.forEach(topicResult -> topicResult.setMsqResultId(msqResult.getId()));
        topicResultMapper.saveBatch(topicResults);
    }

    @Override
    public MsqReviewInfoVO getReviewInfoById(Long msqResultId) {

        MsqResult msqResult = msqResultMapper.selectById(msqResultId);
        List<TopicResultVO> topicResults = topicResultMapper.getTopicResultsByMsqResultId(msqResultId);

        return MsqReviewInfoVO.builder().id(msqResult.getId()).msqName(msqResult.getMsqName()).topicResults(topicResults).build();
    }

    private <T> List<Long> getIds(List<T> list) {
        List<Long> ids = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (T t : list) {
                if (t instanceof Topic) {
                    if (((Topic) t).getId() != null) ids.add(((Topic) t).getId());
                }
            }
        }
        return ids;
    }

    public List<Topic> getTopicParent() {
        return topicMapper.getTopicParent();
    }
}
