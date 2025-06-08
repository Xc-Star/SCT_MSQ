package com.sct.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sct.entity.common.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @Title: Msq
 * @Author Xc_Star
 * @Package com.sct.entity.common
 * @Date 2025/6/6 21:42
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("msq")
public class Msq extends BaseEntity {

    public final static Integer TYPE_REDSTONE = 1;
    public final static Integer TYPE_QUESTIONNAIRE = 2;
    public final static Integer TYPE_OTHER = 3;

    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 问卷名字
     */
    private String name;

    /**
     * 问卷描述
     */
    private String description;

    /**
     * 问卷类型 1-红石 2-建筑 3-其他
     */
    private Integer type;

    /**
     * 问卷状态 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    private Boolean isEnable() {
        return this.status == 1;
    }
}
