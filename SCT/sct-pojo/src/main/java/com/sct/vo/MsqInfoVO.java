package com.sct.vo;

import com.sct.entity.Topic;
import lombok.*;

import java.util.List;

/**
 * @Title: MsqInfoVO
 * @Author Xc_Star
 * @Package com.sct.vo
 * @Date 2025/6/6 23:27
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class MsqInfoVO {

    private Long id;

    private String name;

    private String description;

    private Integer type;

    private List<TopicVO> topics;
}
