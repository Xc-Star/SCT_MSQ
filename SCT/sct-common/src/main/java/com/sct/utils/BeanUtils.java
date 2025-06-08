package com.sct.utils;

import cn.hutool.core.bean.BeanUtil;
import com.sct.result.PageResult;

import java.util.List;
import java.util.function.Consumer;

/**
 * @Title: BeanUtils
 * @Author Xc_Star
 * @Package com.sct.utils
 * @Date 2025/6/9 01:56
 */
public class BeanUtils {

    public static <T> T toBean(Object source, Class<T> targetClass) {
        return BeanUtil.toBean(source, targetClass);
    }

    public static <T> T toBean(Object source, Class<T> targetClass, Consumer<T> peek) {
        T target = toBean(source, targetClass);
        if (target != null) {
            peek.accept(target);
        }
        return target;
    }

    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        BeanUtil.copyProperties(source, target, false);
    }
}
