package com.sct.vo;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 前台展览详情（弹窗用：多图 + 正文）
 *
 * @Title: ExhibitionDetailVO
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
public class ExhibitionDetailVO {

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
     * 封面图地址
     */
    private String cover;

    /**
     * 图片列表，按展示顺序
     */
    private List<String> images;

    /**
     * 正文
     */
    private String content;

    /**
     * 是否置顶 0-否 1-是
     */
    private Boolean top;

    /**
     * 发布时间
     */
    private LocalDateTime createTime;
}
