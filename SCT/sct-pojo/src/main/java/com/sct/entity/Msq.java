package com.sct.entity;

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

    /**
     * 问卷名字
     */
    private String name;

    /**
     * 问卷描述
     */
    private String description;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
