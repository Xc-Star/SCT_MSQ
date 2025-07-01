package com.sct.vo;

import lombok.*;

import java.util.List;

/**
 * @Title: MsqReviewInfoVO
 * @Author Xc_Star
 * @Package com.sct.vo
 * @Date 2025/6/11 18:52
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class MsqReviewInfoVO {

    private Long id;

    private String msqName;

    private Integer status;

    List<TopicResultVO> topicResults;
}
