package com.sct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.sct.entity.common.BaseEntity;
import lombok.*;

import java.util.Collections;
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
@TableName(value = "topic", autoResultMap = true)
public class Topic extends BaseEntity {

    public static final String TYPE_INPUT = "input";
    public static final String TYPE_RADIO = "radio";
    public static final String TYPE_CHECKBOX = "checkbox";
    public static final String TYPE_FILE = "file";

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
     * 题目排序编号
     */
    private Integer no;

    /**
     * 问题类型
     */
    private String type;

    /**
     * 问题
     */
    private String topic;

    /**
     * 图片数量
     */
    private Integer imageCount;

    /**
     * 选项
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<String> options;
}
