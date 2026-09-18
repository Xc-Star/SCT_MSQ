package com.sct.vo;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 前台展览列表项（只带封面，详情点开弹窗时再单独拉）
 *
 * @Title: ExhibitionVO
 * @Author Xc_Star
 * @Package com.sct.vo
 * @Date 2026/9/18 15:40
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ExhibitionVO {

    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 分类 redstone / building / other
     */
    private String category;

    /**
     * 封面图地址，为空时由后端取 images 第一张兜底
     */
    private String cover;

    /**
     * 图片数量
     */
    private Integer imageCount;

    /**
     * 是否置顶 0-否 1-是
     */
    private Boolean top;

    /**
     * 发布时间
     */
    private LocalDateTime createTime;
}
