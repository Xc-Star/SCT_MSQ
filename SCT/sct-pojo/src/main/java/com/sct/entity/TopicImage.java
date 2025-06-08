package com.sct.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sct.entity.common.BaseEntity;
import lombok.*;

/**
 * @Title: TopicImage
 * @Author Xc_Star
 * @Package com.sct.entity
 * @Date 2025/6/6 21:58
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("topic_image")
public class TopicImage extends BaseEntity {

    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 题目id
     */
    private Long topicId;

    /**
     * 图片地址
     */
    private String imageUrl;
}
