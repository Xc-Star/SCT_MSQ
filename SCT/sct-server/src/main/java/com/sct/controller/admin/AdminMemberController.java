package com.sct.controller.admin;

import com.sct.dto.MemberPageQueryDTO;
import com.sct.dto.MemberRemoveDTO;
import com.sct.result.PageResult;
import com.sct.result.Result;
import com.sct.service.MemberService;
import com.sct.vo.MemberListVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Title: AdminMemberController
 * @Author Xc_Star
 * @Package com.sct.controller.admin
 * @Date 2025/6/13 01:57
 */

@RestController
@RequestMapping("/admin/member")
public class AdminMemberController {

    @Resource
    private MemberService memberService;

    @GetMapping("/page")
    public Result<PageResult<MemberListVO>> page(MemberPageQueryDTO memberPageQueryDTO) {
        return Result.success(memberService.pageQuery(memberPageQueryDTO));
    }

    @DeleteMapping("/remove")
    public Result<?> remove(MemberRemoveDTO memberRemoveDTO) {
        memberService.remove(memberRemoveDTO);
        return Result.success();
    }
}
