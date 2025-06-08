package com.sct.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("topic_result")
public class TopicResult extends BaseEntity {

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
     * 问题id
     */
    private Long topicId;

    /**
     * 问题
     */
    private String topicTopic;

    /**
     * 问题结果
     */
    private String topicResult;

    /**
     * 多选结果
     */
    private List<String> topicResults;

    /**
     * 回答者
     */
    private String respondent;

    /**
     * 回答者联系方式
     */
    private String respondentContact;
}
