package com.sct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.sct.entity.common.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 展览内容
 * <p>
 * 机器展览区（红石）、建筑展览区（建筑）、其他内容（其他）三块共用这一张表，
 * 靠 {@link #category} 区分，取值见 {@code CATEGORY_*} 常量。
 *
 * @Title: Exhibition
 * @Author Xc_Star
 * @Package com.sct.entity
 * @Date 2026/9/18 15:40
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value = "exhibition", autoResultMap = true)
public class Exhibition extends BaseEntity {

    /** 分类：红石，对应前台「机器展览区」 */
    public static final String CATEGORY_REDSTONE = "redstone";
    /** 分类：建筑，对应前台「建筑展览区」 */
    public static final String CATEGORY_BUILDING = "building";
    /** 分类：其他，对应前台「其他内容」 */
    public static final String CATEGORY_OTHER = "other";

    /** 状态：正常，对外展示 */
    public static final Integer STATUS_NORMAL = 1;
    /** 状态：隐藏，不对外展示 */
    public static final Integer STATUS_HIDDEN = 0;

    /** 是否置顶：否 */
    public static final Integer TOP_NO = 0;
    /** 是否置顶：是 */
    public static final Integer TOP_YES = 1;

    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 分类 redstone-红石 building-建筑 other-其他
     */
    private String category;

    /**
     * 封面图地址，为空时取 images 的第一张
     */
    private String cover;

    /**
     * 图片列表，按展示顺序存放，库里存 JSON 数组字符串
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    /**
     * 正文
     */
    private String content;

    /**
     * 是否置顶 0-否 1-是
     */
    private Integer top;

    /**
     * 状态 0-隐藏 1-正常
     */
    private Integer status;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 判断分类是否为受支持的三种之一
     */
    public static boolean isSupportedCategory(String category) {
        return CATEGORY_REDSTONE.equals(category)
                || CATEGORY_BUILDING.equals(category)
                || CATEGORY_OTHER.equals(category);
    }
}
