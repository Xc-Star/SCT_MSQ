package com.sct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sct.context.BaseContext;
import com.sct.dto.MsqUpdateDTO;
import com.sct.entity.Msq;
import com.sct.entity.Topic;
import com.sct.exception.BaseException;
import com.sct.mapper.MsqMapper;
import com.sct.mapper.TopicMapper;
import com.sct.service.TopicService;
import com.sct.utils.BeanUtils;
import com.sct.vo.MsqInfoVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    private MsqMapper mapper;
    @Resource
    private TopicMapper topicMapper;
    @Resource
    private MsqMapper msqMapper;

    @Override
    public MsqInfoVO getMsqInfoVO(Long msqId) {
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
            topicMapper.updateById(topic);
            ids.remove(topic.getId());
        }
        if (!ids.isEmpty()) topicMapper.deleteBatchIds(ids);
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
