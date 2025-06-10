package com.sct.vo;

import lombok.*;

/**
 * @Title: LoginVO
 * @Author Xc_Star
 * @Package com.sct.vo
 * @Date 2025/6/9 04:42
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class LoginVO {

    private UserInfoVO userInfo;

    private String token;
}
