package com.sct.dto;

import com.sct.entity.Topic;
import lombok.Data;

import java.util.List;

/**
 * @Title: TopicDTO
 * @Author Xc_Star
 * @Package com.sct.dto
 * @Date 2025/6/26 01:50
 */

@Data
public class TopicDTO extends Topic {

    private List<String> images;
}
