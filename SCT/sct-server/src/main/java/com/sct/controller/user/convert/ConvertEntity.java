package com.sct.controller.user.convert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: ConvertEntity
 * @Author Xc_Star
 * @Package com.sct.controller.user.convert
 * @Date 2025/6/26 23:35
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConvertEntity {
    private String name;
    private int totalRequired;
    private int missingQuantity;
    private int availableQuantity;
}
