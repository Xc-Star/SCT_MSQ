package com.sct.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sct.entity.common.BaseEntity;
import lombok.*;

import java.util.List;

/**
 * @Title: Topic
 * @Author Xc_Star
 * @Package com.sct.entity
 * @Date 2025/6/6 21:55
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("topic")
public class Topic extends BaseEntity {

    public static final String TYPE_INPUT = "input";
    public static final String TYPE_RADIO = "radio";
    public static final String TYPE_CHECKBOX = "checkbox";

    /**
     * 问卷id
     */
    private Long msqId;

    /**
     * 问卷名称
     */
    private String msqName;

    /**
     * 问题类型
     */
    private String type;

    /**
     * 问题
     */
    private String topic;

    /**
     * 选项
     */
    private List<String> options;
}
