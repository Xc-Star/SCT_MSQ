package com.sct.service;

import com.sct.dto.ExhibitionPageQueryDTO;
import com.sct.dto.ExhibitionSaveDTO;
import com.sct.dto.ExhibitionUpdateDTO;
import com.sct.result.PageResult;
import com.sct.vo.ExhibitionAdminVO;
import com.sct.vo.ExhibitionDetailVO;
import com.sct.vo.ExhibitionVO;

import java.util.List;

/**
 * @Title: ExhibitionService
 * @Author Xc_Star
 * @Package com.sct.service
 * @Date 2026/9/18 15:40
 */
public interface ExhibitionService {

    /**
     * 查询对外展示的展览列表（置顶优先，其次按发布时间倒序）
     *
     * @param category 分类 redstone / building / other，为空表示不筛选
     * @param limit    返回条数，为空或小于等于 0 时返回全部
     * @return 展览列表（只带封面）
     */
    List<ExhibitionVO> list(String category, Integer limit);

    /**
     * 查询单条展览详情（弹窗用：多图 + 正文）
     *
     * @param id 展览id
     * @return 展览详情
     */
    ExhibitionDetailVO detail(Long id);

    /**
     * 后台分页查询展览内容
     *
     * @param dto 分页与筛选条件
     * @return 分页结果
     */
    PageResult<ExhibitionAdminVO> pageQuery(ExhibitionPageQueryDTO dto);

    /**
     * 后台新增展览内容
     *
     * @param dto 展览内容
     * @return 新增记录的 id
     */
    Long save(ExhibitionSaveDTO dto);

    /**
     * 后台修改展览内容
     *
     * @param dto id 必填，其余字段为 null 表示不修改
     */
    void update(ExhibitionUpdateDTO dto);

    /**
     * 后台删除展览内容（逻辑删除）
     *
     * @param id 展览id
     */
    void remove(Long id);
}
