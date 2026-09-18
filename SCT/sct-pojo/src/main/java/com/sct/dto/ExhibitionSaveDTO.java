package com.sct.dto;

import lombok.Data;

import java.util.List;

/**
 * 后台新增展览内容
 *
 * @Title: ExhibitionSaveDTO
 * @Author Xc_Star
 * @Package com.sct.dto
 * @Date 2026/9/18 15:40
 */

@Data
public class ExhibitionSaveDTO {

    /**
     * 标题，必填
     */
    private String title;

    /**
     * 分类 redstone-红石 building-建筑 other-其他，必填
     */
    private String category;

    /**
     * 封面图地址，选填；为空时后端自动取 images 的第一张
     */
    private String cover;

    /**
     * 图片列表，必填且至少一张
     */
    private List<String> images;

    /**
     * 正文，必填
     */
    private String content;

    /**
     * 是否置顶 0-否 1-是，不传按 0
     */
    private Integer top;

    /**
     * 状态 0-隐藏 1-正常，不传按 1
     */
    private Integer status;
}
