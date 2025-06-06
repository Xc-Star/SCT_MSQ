package com.sct.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sct.entity.common.BaseEntity;
import lombok.*;

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

    public static final Integer STATUS_WAITING = 0; // 等待审核
    public static final Integer STATUS_PASS = 1; // 通过
    public static final Integer STATUS_FAIL = 2; // 未通过

    /**
     * 回答者
     */
    private String respondent;

    /**
     * 回答者联系方式
     */
    private String respondentContact;

    /**
     * 问卷状态
     */
    private Integer status;
}
