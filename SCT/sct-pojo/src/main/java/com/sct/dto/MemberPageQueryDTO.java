package com.sct.dto;

import com.sct.entity.common.PageParam;
import lombok.Data;

/**
 * @Title: MemberPageQueryDTO
 * @Author Xc_Star
 * @Package com.sct.dto
 * @Date 2025/6/13 02:02
 */

@Data
public class MemberPageQueryDTO extends PageParam {

    private Boolean isRemoved;
}
