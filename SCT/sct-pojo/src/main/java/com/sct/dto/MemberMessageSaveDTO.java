package com.sct.dto;

import lombok.Data;

/**
 * 后台手动新增留言
 *
 * @Title: MemberMessageSaveDTO
 * @Author Xc_Star
 * @Package com.sct.dto
 * @Date 2026/9/18 10:48
 */

@Data
public class MemberMessageSaveDTO {

    /**
     * 玩家ID（游戏内ID），必填
     */
    private String playerId;

    /**
     * QQ号，选填
     */
    private String qq;

    /**
     * 留言内容，必填
     */
    private String content;

    /**
     * 是否置顶 0-否 1-是，不传按 0
     */
    private Integer top;

    /**
     * 状态 0-隐藏 1-正常，不传按 1
     */
    private Integer status;
}
