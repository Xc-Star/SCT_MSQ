package com.sct.enumeration;

import cn.hutool.core.util.ArrayUtil;
import com.sct.utils.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Title: ConfigKeyEnum
 * @Author Xc_Star
 * @Package com.sct.enumeration
 * @Date 2025/6/30 03:35
 */

@Getter
@AllArgsConstructor
public enum ConfigKeyEnum {

    LOGO("logo", "服务器Logo"),
    SERVER_NAME("server_name", "服务器名称"),
    MAIN_TITLE("main_title", "主页标题"),
    MAIN_DESCRIPTION("main_description", "主页描述"),
    MAIN_BACKGROUND("main_background", "主页背景"),
    MSQ_BACKGROUND("msq_background", "问卷背景"),
    STOCK_LIST_TOOL("stock_list_tool", "是否开启备货列表工具"),
    REDSTONE_MSQ("redstone_msq", "是否开启红石问卷"),
    ARCHITECTURAL_MSQ("architectural_msq", "是否开启建筑问卷"),
    LOGISTICS_MSQ("logistics_msq", "是否开启后勤问卷"),
    OTHER_MSQ("other_msq", "是否开启其他问卷");

    private final String keyName;

    private final String description;
}
