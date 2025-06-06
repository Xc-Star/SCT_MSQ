package com.sct.entity.common;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Title: BaseEntity
 * @Author Xc_Star
 * @Package com.sct.entity.common
 * @Date 2025/6/6 21:40
 */

@Data
public class BaseEntity {
    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 逻辑删除
     */
    private Boolean deleted;
}
