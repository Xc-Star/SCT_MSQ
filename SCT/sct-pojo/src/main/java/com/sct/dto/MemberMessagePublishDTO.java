package com.sct.dto;

import lombok.Data;

/**
 * @Title: MemberMessagePublishDTO
 * @Author Xc_Star
 * @Package com.sct.dto
 * @Date 2026/9/18 10:20
 */

@Data
public class MemberMessagePublishDTO {

    /**
     * 玩家ID（游戏内ID），必填
     */
    private String playerId;

    /**
     * QQ号，必填。机器人插件从消息事件里取，用于后续身份核对
     */
    private String qq;

    /**
     * 留言内容，必填
     */
    private String content;
}
