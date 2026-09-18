package com.sct.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sct.entity.common.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @Title: MemberMessage
 * @Author Xc_Star
 * @Package com.sct.entity
 * @Date 2026/9/18 10:20
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("member_message")
public class MemberMessage extends BaseEntity {

    /** 状态：正常，对外展示 */
    public static final Integer STATUS_NORMAL = 1;
    /** 状态：隐藏，不对外展示 */
    public static final Integer STATUS_HIDDEN = 0;

    /** 是否置顶：否 */
    public static final Integer TOP_NO = 0;
    /** 是否置顶：是 */
    public static final Integer TOP_YES = 1;

    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 玩家ID（游戏内ID，用于展示名字与头像）
     */
    private String playerId;

    /**
     * QQ号（对接机器人插件用，不对外展示）
     */
    private String qq;

    /**
     * 留言内容
     */
    private String content;

    /**
     * 是否置顶 0-否 1-是
     */
    private Integer top;

    /**
     * 状态 0-隐藏 1-正常
     */
    private Integer status;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
