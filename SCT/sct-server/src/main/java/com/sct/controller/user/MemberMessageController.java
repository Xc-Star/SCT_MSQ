package com.sct.controller.user;

import com.sct.dto.MemberMessagePublishDTO;
import com.sct.result.Result;
import com.sct.service.MemberMessageService;
import com.sct.vo.MemberMessageVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 成员留言
 *
 * @Title: MemberMessageController
 * @Author Xc_Star
 * @Package com.sct.controller.user
 * @Date 2026/9/18 10:20
 */

@RestController
@RequestMapping("/message")
public class MemberMessageController {

    @Resource
    private MemberMessageService memberMessageService;

    /**
     * 留言列表（官网展示用，公开）
     *
     * @param limit 可选，只取前 N 条，不传则返回全部（最多 200 条）
     */
    @GetMapping("/list")
    public Result<List<MemberMessageVO>> list(@RequestParam(required = false) Integer limit) {
        return Result.success(memberMessageService.list(limit));
    }

    /**
     * 发布留言（供 QQ 机器人插件调用，公开接口，用密钥校验）
     * <p>
     * 请求头需带 X-Api-Key（也可以用 query 参数 key 传），密钥对应 config 表中 message_api_key 的值。
     * 请求体 JSON：{"playerId":"Xc_Star","qq":"123456","content":"大家好"}
     * 其中 playerId 可以不传，此时会按 qq 找该 QQ 最近一次用过的玩家ID。
     *
     * @return 新留言的 id
     */
    @PostMapping("/publish")
    public Result<Long> publish(@RequestBody MemberMessagePublishDTO dto,
                                @RequestHeader(value = "X-Api-Key", required = false) String headerKey,
                                @RequestParam(value = "key", required = false) String paramKey) {
        memberMessageService.checkApiKey(headerKey != null ? headerKey : paramKey);
        return Result.success(memberMessageService.publish(dto));
    }
}
