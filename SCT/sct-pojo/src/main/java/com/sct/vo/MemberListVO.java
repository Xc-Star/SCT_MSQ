package com.sct.vo;

import lombok.*;

import java.time.LocalDateTime;

/**
 * @Title: MemberListVO
 * @Author Xc_Star
 * @Package com.sct.vo
 * @Date 2025/6/13 02:01
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class MemberListVO {

    /**
     * id
     */
    private Long id;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 玩家
     */
    private String respondent;

    /**
     * 玩家联系方式
     */
    private String respondentContact;

    /**
     * 玩家uuid
     */
    private String uuid;

    /**
     * 玩家类型
     */
    private Integer type;

    /**
     * 审核人
     */
    private String reviewer;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 移除人
     */
    private String remover;

    /**
     * 移除时间
     */
    private LocalDateTime removeTime;

    /**
     * 移除原因
     */
    private String remark;
}
