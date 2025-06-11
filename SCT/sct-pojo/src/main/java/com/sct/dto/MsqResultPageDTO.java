package com.sct.dto;

import com.sct.entity.common.PageParam;
import lombok.Data;

/**
 * @Title: MsqResultPageDTO
 * @Author Xc_Star
 * @Package com.sct.dto
 * @Date 2025/6/11 16:38
 */

@Data
public class MsqResultPageDTO extends PageParam {

    private String respondent;

    private String respondentContact;

    private Integer type;
}
