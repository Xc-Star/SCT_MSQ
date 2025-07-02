package com.sct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sct.context.BaseContext;
import com.sct.dto.MsqSubmitDTO;
import com.sct.dto.MsqUpdateDTO;
import com.sct.dto.TopicDTO;
import com.sct.entity.*;
import com.sct.exception.BaseException;
import com.sct.mapper.*;
import com.sct.service.TopicService;
import com.sct.utils.BeanUtils;
import com.sct.vo.MsqInfoVO;
import com.sct.vo.MsqReviewInfoVO;
import com.sct.vo.TopicResultVO;
import com.sct.vo.TopicVO;
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
    @Resource
    private TopicImageMapper topicImageMapper;

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
        List<TopicVO> topicVOS = new ArrayList<>();
        for (Topic topic : topics) {
            if (topic.getImageCount() == null || topic.getImageCount() == 0) {
                topicVOS.add(BeanUtils.toBean(topic, TopicVO.class));
                continue;
            }
            List<TopicImage> images = topicImageMapper.selectList(
                    new QueryWrapper<TopicImage>().eq("topic_id", topic.getId()));
            TopicVO topicVO = BeanUtils.toBean(topic, TopicVO.class);
            topicVO.setImages(images);
            topicVOS.add(topicVO);
        }
        msqInfoVO.setTopics(topicVOS);
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

        List<TopicDTO> topics = msqUpdateDTO.getTopics();
        List<Long> ids = getIds(topics);

        for (TopicDTO topic : topics) {
            if (topic.getId() == null) {
                topic.setMsqId(msq.getId());
                topic.setMsqName(msq.getName());
                Topic bean = BeanUtils.toBean(topic, Topic.class);
                if (topic.getImages() != null && !topic.getImages().isEmpty()) {
                    bean.setImageCount(topic.getImages().size());
                } else {
                    bean.setImageCount(0);
                }
                topicMapper.insert(bean);
                if (topic.getImages() != null && !topic.getImages().isEmpty()) {
                    saveBatchImage(bean.getId(), topic.getImages());
                }
                continue;
            }
            if (topic.getId() < 0) {
                ids.remove(topic.getId());
                continue;
            }
            topic.setId(null);
            topic.setMsqId(msq.getId());
            topic.setMsqName(msq.getName());
            Topic bean = BeanUtils.toBean(topic, Topic.class);
            if (topic.getImages() != null && !topic.getImages().isEmpty()) {
                bean.setImageCount(topic.getImages().size());
            } else {
                bean.setImageCount(0);
            }
            topicMapper.insert(bean);
            if (topic.getImages() != null && !topic.getImages().isEmpty()) {
                saveBatchImage(bean.getId(), topic.getImages());
            }
        }
        if (!ids.isEmpty()) topicMapper.deleteBatchIds(ids);
    }

    private void saveBatchImage(Long topicId, List<String> images) {
        for (String image : images) {
            TopicImage topicImage = new TopicImage();
            topicImage.setTopicId(topicId);
            topicImage.setImageUrl(image);
            topicImage.setCreateTime(LocalDateTime.now());
            topicImageMapper.insert(topicImage);
        }
    }

    @Transactional
    @Override
    public void submit(MsqSubmitDTO msqSubmitDTO) {
        List<TopicResult> topicResults = msqSubmitDTO.getTopicResults();
        String respondent = topicResults.get(0).getTopicResult();
        String respondentContact = topicResults.get(1).getTopicResult();
        LocalDateTime now = LocalDateTime.now();
        MsqResult one = msqResultMapper.selectOne(new QueryWrapper<MsqResult>().eq("respondent", respondent).eq("status", MsqResult.STATUS_REMOVED));
        if (one != null) {
            throw new BaseException("您已被移除服务器");
        }
        List<MsqResult> msqResults = msqResultMapper.selectList(
                new QueryWrapper<MsqResult>()
                        .eq("respondent", respondent)
                        .eq("type", msqSubmitDTO.getType())
                        .between("create_time", now.minusDays(30), now));
        if (msqResults != null && !msqResults.isEmpty()) {
            throw new BaseException("您已提交过问卷，请联系管理员审核！");
        }
        MsqResult msqResult = MsqResult.builder()
                .msqId(msqSubmitDTO.getMsqId())
                .msqName(msqSubmitDTO.getName())
                .respondent(respondent)
                .respondentContact(respondentContact)
                .uuid(msqSubmitDTO.getUuid())
                .type(msqSubmitDTO.getType())
                .status(MsqResult.STATUS_WAITING)
                .build();
        msqResultMapper.insert(msqResult);
        topicResults.forEach(topicResult -> topicResult.setMsqResultId(msqResult.getId()));
        topicResultMapper.saveBatch(topicResults);
    }

    @Override
    public MsqReviewInfoVO getReviewInfoById(Long msqResultId) {

        MsqResult msqResult = msqResultMapper.selectById(msqResultId);
        List<TopicResultVO> topicResults = topicResultMapper.getTopicResultsByMsqResultId(msqResultId);

        for (TopicResultVO topicResult : topicResults) {
            if (topicResult.getImageCount() != null && topicResult.getImageCount() != 0) {
                List<TopicImage> images = topicImageMapper.selectList(
                        new QueryWrapper<TopicImage>().eq("topic_id", topicResult.getTopicId()));
                topicResult.setImages(images);
            }
            if ("file".equals(topicResult.getType())) {
                TopicResult topicResult1 = topicResultMapper.selectById(topicResult.getId());
                topicResult.setFiles(topicResult1.getFiles());
            }
        }

        return MsqReviewInfoVO.builder().id(msqResult.getId()).msqName(msqResult.getMsqName()).status(msqResult.getStatus()).topicResults(topicResults).build();
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
