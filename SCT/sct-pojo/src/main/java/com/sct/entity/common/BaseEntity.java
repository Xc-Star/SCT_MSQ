package com.sct.entity.common;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

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
    @TableLogic
    private Boolean deleted;
}
