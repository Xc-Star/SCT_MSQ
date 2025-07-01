package com.sct.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sct.entity.common.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Title: Config
 * @Author Xc_Star
 * @Package com.sct.entity
 * @Date 2025/6/30 03:22
 */

@Data
@TableName("config")
public class Config extends BaseEntity {

    public static final Integer CONFIG_TYPE_TEXT = 1; // 文本
    public static final Integer CONFIG_TYPE_IMAGE = 2; // 图片
    public static final Integer CONFIG_TYPE_SWITCH = 3; // 开关

    private Long id;

    private String configName;

    private String configKey;

    private String configValue;

    private Integer type;

    private LocalDateTime updateTime;
}
