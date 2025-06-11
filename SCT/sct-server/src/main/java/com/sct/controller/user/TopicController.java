package com.sct.controller.user;

import com.sct.dto.MsqSubmitDTO;
import com.sct.entity.Msq;
import com.sct.result.Result;
import com.sct.service.TopicService;
import com.sct.vo.MsqInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @GetMapping("/getOne/{msqId}")
    public Result<MsqInfoVO> getOne(@PathVariable Long msqId) {
        return Result.success(topicService.getOneMsqInfoVO(msqId));
    }

    @GetMapping("/getType/{type}")
    public Result<?> getMsq(@PathVariable String type,
                            HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Msq> msqList = topicService.getMsqInfoVO(type);
        if (msqList.size() == 1) {
            request.getRequestDispatcher("/topic/getOne/" + msqList.get(0).getId()).forward(request, response);
            return Result.success();
        }
        request.getRequestDispatcher("/msq/selectMsq/" + type).forward(request, response);
        return Result.success();
    }

    @PostMapping("/submit")
    public Result<?> submit(@RequestBody MsqSubmitDTO msqSubmitDTO) {
        topicService.submit(msqSubmitDTO);
        return Result.success();
    }
}
