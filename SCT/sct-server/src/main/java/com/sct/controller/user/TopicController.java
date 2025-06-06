package com.sct.controller.user;

import com.sct.entity.Topic;
import com.sct.result.Result;
import com.sct.vo.MsqInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: TopicController
 * @Author Xc_Star
 * @Package com.sct.controller.user
 * @Date 2025/6/6 23:21
 */

@RestController
@RequestMapping("/topic")
@Slf4j
public class TopicController {

    @GetMapping("/get/{msqId}")
    public Result<?> get(@PathVariable Long msqId) {
        MsqInfoVO msqInfoVO = new MsqInfoVO(1L, "SCT问卷", "SCT的一些碎碎念，可以要也可以不要的，比如可以介绍一下服务器的特色什么的，也可以介绍一下问卷啥的", null);
        List<Topic> topics = new ArrayList<>();
        Topic topic1 = new Topic(1L, "SCT问卷", "input", "请输入你的正版ID", null);
        topic1.setId(1L);
        Topic topic2 = new Topic(2L, "SCT问卷", "radio", "你的性别", List.of("男", "女", "草履虫", "沃尔玛购物袋", "其他"));
        topic2.setId(2L);
        Topic topic3 = new Topic(3L, "SCT问卷", "checkbox", "你的爱好", List.of("唱", "跳", "rap", "篮球"));
        topic3.setId(3L);
        topics.add(topic1);
        topics.add(topic2);
        topics.add(topic3);
        msqInfoVO.setTopics(topics);
        return Result.success(msqInfoVO);
    }
}
