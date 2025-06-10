package com.sct.controller.user;

import com.sct.entity.Topic;
import com.sct.result.Result;
import com.sct.service.TopicService;
import com.sct.vo.MsqInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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

    @Resource
    private TopicService topicService;

    @GetMapping("/get/{msqId}")
    public Result<MsqInfoVO> get(@PathVariable Long msqId) {
        return Result.success(topicService.getMsqInfoVO(msqId));
    }
}
