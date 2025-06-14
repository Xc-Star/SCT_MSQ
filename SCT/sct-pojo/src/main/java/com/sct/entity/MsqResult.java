package com.sct.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sct.entity.common.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @Title: MsqResult
 * @Author Xc_Star
 * @Package com.sct.entity
 * @Date 2025/6/6 21:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("msq_result")
public class MsqResult extends BaseEntity {

    public static final Integer STATUS_WAITING = 1; // 等待审核
    public static final Integer STATUS_PASS = 2; // 通过
    public static final Integer STATUS_FAIL = 3; // 未通过
    public static final Integer STATUS_REMOVED = 4; // 已移出

    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 问卷id
     */
    private Long msqId;

    /**
     * 问卷名称
     */
    private String msqName;

    /**
     * 问卷类型 1-红石 2-建筑 3-后勤 4-其他
     */
    private Integer type;

    /**
     * 回答者
     */
    private String respondent;

    /**
     * 回答者联系方式
     */
    private String respondentContact;

    /**
     * uuid
     */
    private String uuid;

    /**
     * 问卷状态
     */
    private Integer status;

    /**
     * 审核人id
     */
    private Long reviewerId;

    /**
     * 审核人
     */
    private String reviewer;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 移出人id
     */
    private Long removerId;

    /**
     * 移出人
     */
    private String remover;

    /**
     * 移出时间
     */
    private LocalDateTime removeTime;
}
