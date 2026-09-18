package com.sct.vo;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台展览列表用的 VO，带全量字段，编辑弹窗可以直接回填
 *
 * @Title: ExhibitionAdminVO
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
public class ExhibitionAdminVO {

    private Long id;

    private String title;

    /**
     * 分类 redstone / building / other
     */
    private String category;

    private String cover;

    private List<String> images;

    private String content;

    /**
     * 是否置顶 0-否 1-是
     */
    private Integer top;

    /**
     * 状态 0-隐藏 1-正常
     */
    private Integer status;

    private LocalDateTime createTime;

    private String remark;
}
