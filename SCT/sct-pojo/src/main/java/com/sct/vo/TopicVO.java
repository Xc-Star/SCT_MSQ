package com.sct.vo;

import com.sct.entity.Topic;
import com.sct.entity.TopicImage;
import lombok.Data;

import java.util.List;

/**
 * @Title: TopicVO
 * @Author Xc_Star
 * @Package com.sct.vo
 * @Date 2025/6/26 02:14
 */

@Data
public class TopicVO extends Topic {

    private List<TopicImage> images;
}
