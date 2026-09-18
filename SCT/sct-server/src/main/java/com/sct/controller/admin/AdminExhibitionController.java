package com.sct.controller.admin;

import com.sct.dto.ExhibitionPageQueryDTO;
import com.sct.dto.ExhibitionSaveDTO;
import com.sct.dto.ExhibitionUpdateDTO;
import com.sct.result.PageResult;
import com.sct.result.Result;
import com.sct.service.ExhibitionService;
import com.sct.vo.ExhibitionAdminVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 后台展览内容管理（机器展览区 / 建筑展览区 / 其他内容）
 *
 * @Title: AdminExhibitionController
 * @Author Xc_Star
 * @Package com.sct.controller.admin
 * @Date 2026/9/18 15:40
 */

@RestController
@RequestMapping("/admin/exhibition")
public class AdminExhibitionController {

    @Resource
    private ExhibitionService exhibitionService;

    /**
     * 分页查询展览内容，可按关键词（标题 / 正文）、分类和状态筛选
     */
    @GetMapping("/page")
    public Result<PageResult<ExhibitionAdminVO>> page(ExhibitionPageQueryDTO dto) {
        return Result.success(exhibitionService.pageQuery(dto));
    }

    /**
     * 新增展览内容
     *
     * @return 新增记录的 id
     */
    @PostMapping
    public Result<Long> save(@RequestBody ExhibitionSaveDTO dto) {
        return Result.success(exhibitionService.save(dto));
    }

    /**
     * 修改展览内容，字段为 null 表示不改
     */
    @PutMapping
    public Result<?> update(@RequestBody ExhibitionUpdateDTO dto) {
        exhibitionService.update(dto);
        return Result.success();
    }

    /**
     * 删除展览内容（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<?> remove(@PathVariable Long id) {
        exhibitionService.remove(id);
        return Result.success();
    }
}
