package com.sct.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.*;

import java.util.List;

/**
 * @Title: TopicResultVO
 * @Author Xc_Star
 * @Package com.sct.vo
 * @Date 2025/6/11 19:09
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TopicResultVO {

    private String topic;

    private String type;

    private String options;

    private String topicResult;

    private String topicResults;
}
