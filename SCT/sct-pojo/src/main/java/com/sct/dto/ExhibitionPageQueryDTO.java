package com.sct.dto;

import com.sct.entity.common.PageParam;
import lombok.Data;

/**
 * 后台展览内容分页查询
 *
 * @Title: ExhibitionPageQueryDTO
 * @Author Xc_Star
 * @Package com.sct.dto
 * @Date 2026/9/18 15:40
 */

@Data
public class ExhibitionPageQueryDTO extends PageParam {

    /**
     * 关键词，匹配标题与正文
     */
    private String keyword;

    /**
     * 分类筛选 redstone / building / other，为空表示全部
     */
    private String category;

    /**
     * 状态筛选 0-隐藏 1-正常，为空表示全部
     */
    private Integer status;
}
