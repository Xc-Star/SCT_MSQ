package com.sct.controller.user;

import com.sct.result.Result;
import com.sct.service.ExhibitionService;
import com.sct.vo.ExhibitionDetailVO;
import com.sct.vo.ExhibitionVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 展览内容（机器展览区 / 建筑展览区 / 其他内容），官网展示用，公开接口
 *
 * @Title: ExhibitionController
 * @Author Xc_Star
 * @Package com.sct.controller.user
 * @Date 2026/9/18 15:40
 */

@RestController
@RequestMapping("/exhibition")
public class ExhibitionController {

    @Resource
    private ExhibitionService exhibitionService;

    /**
     * 展览列表
     *
     * @param category 分类 redstone-红石（机器展览区）/ building-建筑（建筑展览区）/ other-其他（其他内容），不传返回全部
     * @param limit    可选，只取前 N 条，不传则返回全部（最多 200 条）
     */
    @GetMapping("/list")
    public Result<List<ExhibitionVO>> list(@RequestParam(required = false) String category,
                                           @RequestParam(required = false) Integer limit) {
        return Result.success(exhibitionService.list(category, limit));
    }

    /**
     * 展览详情（点开弹窗时调用，带多图与正文）
     */
    @GetMapping("/{id}")
    public Result<ExhibitionDetailVO> detail(@PathVariable Long id) {
        return Result.success(exhibitionService.detail(id));
    }
}
