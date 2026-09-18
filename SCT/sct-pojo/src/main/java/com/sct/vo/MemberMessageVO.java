package com.sct.vo;

import lombok.*;

import java.time.LocalDateTime;

/**
 * @Title: MemberMessageVO
 * @Author Xc_Star
 * @Package com.sct.vo
 * @Date 2026/9/18 10:20
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class MemberMessageVO {

    private Long id;

    /**
     * 玩家ID（游戏内ID）
     */
    private String playerId;

    /**
     * 头像地址，由 playerId 拼接得到
     */
    private String avatar;

    /**
     * 留言内容
     */
    private String content;

    /**
     * 是否置顶 0-否 1-是
     */
    private Boolean top;

    private LocalDateTime createTime;
}
