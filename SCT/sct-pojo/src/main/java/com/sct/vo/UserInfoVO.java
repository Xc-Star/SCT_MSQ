package com.sct.vo;

import lombok.*;

/**
 * @Title: UserInfoVO
 * @Author Xc_Star
 * @Package com.sct.vo
 * @Date 2025/6/9 04:43
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserInfoVO {

    private Long id;

    private String username;

    private String avatar;

    private Boolean isSuperAdmin;
}
