package com.sct.controller.admin;

import com.sct.entity.MsqResult;
import com.sct.entity.Topic;
import com.sct.result.Result;
import com.sct.vo.MsqInfoVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: AdminTopicController
 * @Author Xc_Star
 * @Package com.sct.controller.admin
 * @Date 2025/6/6 22:28
 */

@RestController
@RequestMapping("/admin/topic")
public class AdminTopicController {

    @GetMapping("/list")
    public Result<MsqResult> list() {
        return Result.success(new MsqResult());
    }



    @GetMapping("/get/{msqId}")
    public Result<?> get2(@PathVariable Long msqId) {
        MsqInfoVO msqInfoVO = new MsqInfoVO(1L, "SCT问卷", "SCT的一些碎碎念，可以要也可以不要的，比如可以介绍一下服务器的特色什么的，也可以介绍一下问卷啥的", null);
        List<Topic> topics = new ArrayList<>();
        Topic topic1 = new Topic(1L, 1L, "SCT问卷", "input", "请输入你的正版ID", null);
        Topic topic2 = new Topic(2L, 1L, "SCT问卷", "radio", "你的性别", List.of("男", "女", "草履虫", "沃尔玛购物袋", "其他"));
        Topic topic3 = new Topic(3L, 1L, "SCT问卷", "checkbox", "你的爱好", List.of("唱", "跳", "rap", "篮球"));
        topics.add(topic1);
        topics.add(topic2);
        topics.add(topic3);
        msqInfoVO.setTopics(topics);
        return Result.success(msqInfoVO);
    }
}
