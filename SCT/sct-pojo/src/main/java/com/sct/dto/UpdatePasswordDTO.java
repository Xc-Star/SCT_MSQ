package com.sct.dto;

import lombok.Data;

/**
 * @Title: UpdatePasswordDTO
 * @Author Xc_Star
 * @Package com.sct.dto
 * @Date 2025/6/9 01:33
 */

@Data
public class UpdatePasswordDTO {

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

}
