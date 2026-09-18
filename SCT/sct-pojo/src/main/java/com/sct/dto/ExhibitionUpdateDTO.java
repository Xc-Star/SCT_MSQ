package com.sct.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 后台修改展览内容
 * <p>
 * 字段与新增一致，额外带 id；其中 id 必填，其余字段为 null 表示不修改。
 *
 * @Title: ExhibitionUpdateDTO
 * @Author Xc_Star
 * @Package com.sct.dto
 * @Date 2026/9/18 15:40
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ExhibitionUpdateDTO extends ExhibitionSaveDTO {

    /**
     * 展览内容id，必填
     */
    private Long id;
}
