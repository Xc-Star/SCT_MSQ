package com.sct.vo;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 后台留言列表用的 VO，比前台多出 QQ 号和状态，方便运营核对与屏蔽
 *
 * @Title: MemberMessageAdminVO
 * @Author Xc_Star
 * @Package com.sct.vo
 * @Date 2026/9/18 10:40
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class MemberMessageAdminVO {

    private Long id;

    private String playerId;

    private String avatar;

    /**
     * QQ号
     */
    private String qq;

    private String content;

    /**
     * 是否置顶 0-否 1-是
     */
    private Integer top;

    /**
     * 状态 0-隐藏 1-正常
     */
    private Integer status;

    private LocalDateTime createTime;

    private String remark;
}
