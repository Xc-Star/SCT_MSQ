package com.sct.dto;

import com.sct.entity.common.PageParam;
import lombok.Data;

/**
 * @Title: MemberMessagePageQueryDTO
 * @Author Xc_Star
 * @Package com.sct.dto
 * @Date 2026/9/18 10:40
 */

@Data
public class MemberMessagePageQueryDTO extends PageParam {

    /**
     * 关键词，匹配玩家ID / 留言内容 / QQ号
     */
    private String keyword;

    /**
     * 状态筛选 0-隐藏 1-正常，为空表示全部
     */
    private Integer status;
}
