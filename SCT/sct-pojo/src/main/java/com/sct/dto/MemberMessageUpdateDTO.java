package com.sct.dto;

import lombok.Data;

/**
 * 后台修改留言的置顶 / 显示状态，字段为 null 表示不修改
 *
 * @Title: MemberMessageUpdateDTO
 * @Author Xc_Star
 * @Package com.sct.dto
 * @Date 2026/9/18 10:40
 */

@Data
public class MemberMessageUpdateDTO {

    private Long id;

    /**
     * 是否置顶 0-否 1-是
     */
    private Integer top;

    /**
     * 状态 0-隐藏 1-正常
     */
    private Integer status;
}
