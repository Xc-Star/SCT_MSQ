package com.sct.dto;

import lombok.Data;

/**
 * @Title: MemberRemoveDTO
 * @Author Xc_Star
 * @Package com.sct.dto
 * @Date 2025/6/13 16:01
 */

@Data
public class MemberRemoveDTO {

    /**
     * id
     */
    private Long id;

    /**
     * 移除原因
     */
    private String remark;
}
