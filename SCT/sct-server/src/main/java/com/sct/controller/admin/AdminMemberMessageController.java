package com.sct.controller.admin;

import com.sct.dto.MemberMessagePageQueryDTO;
import com.sct.dto.MemberMessageSaveDTO;
import com.sct.dto.MemberMessageUpdateDTO;
import com.sct.result.PageResult;
import com.sct.result.Result;
import com.sct.service.MemberMessageService;
import com.sct.vo.MemberMessageAdminVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 后台成员留言管理
 *
 * @Title: AdminMemberMessageController
 * @Author Xc_Star
 * @Package com.sct.controller.admin
 * @Date 2026/9/18 10:40
 */

@RestController
@RequestMapping("/admin/message")
public class AdminMemberMessageController {

    @Resource
    private MemberMessageService memberMessageService;

    /**
     * 分页查询留言，可按关键词（玩家ID / 内容 / QQ）和状态筛选
     */
    @GetMapping("/page")
    public Result<PageResult<MemberMessageAdminVO>> page(MemberMessagePageQueryDTO dto) {
        return Result.success(memberMessageService.pageQuery(dto));
    }

    /**
     * 后台手动新增留言
     *
     * @return 新留言的 id
     */
    @PostMapping
    public Result<Long> save(@RequestBody MemberMessageSaveDTO dto) {
        return Result.success(memberMessageService.save(dto));
    }

    /**
     * 修改置顶 / 显示状态，字段为 null 表示不改
     */
    @PutMapping
    public Result<?> update(@RequestBody MemberMessageUpdateDTO dto) {
        memberMessageService.update(dto);
        return Result.success();
    }

    /**
     * 删除留言（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<?> remove(@PathVariable Long id) {
        memberMessageService.remove(id);
        return Result.success();
    }
}
