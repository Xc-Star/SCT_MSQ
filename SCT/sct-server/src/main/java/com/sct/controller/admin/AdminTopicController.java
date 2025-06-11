package com.sct.controller.admin;

import com.sct.dto.MsqUpdateDTO;
import com.sct.entity.MsqResult;
import com.sct.entity.Topic;
import com.sct.result.Result;
import com.sct.service.TopicService;
import com.sct.vo.MsqInfoVO;
import com.sct.vo.MsqReviewInfoVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

    @Resource
    private TopicService topicService;

    @GetMapping("/list")
    public Result<MsqResult> list() {
        return Result.success(new MsqResult());
    }

    @GetMapping("/get/{msqId}")
    public Result<?> getMsqInfo(@PathVariable Long msqId) {
        return Result.success(topicService.getOneMsqInfoVO(msqId));
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody MsqUpdateDTO msqUpdateDTO) {
        topicService.updateById(msqUpdateDTO);
        return Result.success();
    }

    @GetMapping("/getReviewInfo/{msqResultId}")
    public Result<MsqReviewInfoVO> getReviewInfo(@PathVariable Long msqResultId) {
        return Result.success(topicService.getReviewInfoById(msqResultId));
    }
}
