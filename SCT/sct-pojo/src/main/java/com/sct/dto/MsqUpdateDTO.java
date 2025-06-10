package com.sct.dto;

import com.sct.entity.Topic;
import lombok.Data;

import java.util.List;

/**
 * @Title: MsqUpdateDTO
 * @Author Xc_Star
 * @Package com.sct.dto
 * @Date 2025/6/11 02:59
 */

@Data
public class MsqUpdateDTO {

    private Long id;

    private String name;

    private String description;

    private Integer type;

    private List<Topic> topics;
}
