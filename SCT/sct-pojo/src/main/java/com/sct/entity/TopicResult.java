package com.sct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.sct.entity.common.BaseEntity;
import lombok.*;

import java.util.List;

/**
 * @Title: TopicResult
 * @Author Xc_Star
 * @Package com.sct.entity
 * @Date 2025/6/6 22:00
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value = "topic_result", autoResultMap = true)
public class TopicResult extends BaseEntity {

    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 问卷结果id
     */
    private Long msqResultId;

    /**
     * 问题id
     */
    private Long topicId;

    /**
     * 问题结果
     */
    private String topicResult;

    /**
     * 多选结果
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<String> topicResults;

    /**
     * 文件
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<String> files;
}
