package com.sct.dto;

import com.sct.entity.TopicResult;
import lombok.Data;

import java.util.List;

/**
 * @Title: MsqSubmiuDTO
 * @Author Xc_Star
 * @Package com.sct.dto
 * @Date 2025/6/11 15:41
 */

@Data
public class MsqSubmitDTO {

    private Long msqId;

    private String name;

    private Integer type;

    private String avatar;

    private String uuid;

    private List<TopicResult> topicResults;
}
