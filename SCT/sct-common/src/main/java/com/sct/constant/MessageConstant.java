package com.sct.constant;

/**
 * 信息提示常量类
 */
public class MessageConstant {

    public static final String PASSWORD_ERROR = "密码错误";
    public static final String ACCOUNT_NOT_FOUND = "账号不存在";
    public static final String ACCOUNT_LOCKED = "账号被锁定";
    public static final String UNKNOWN_ERROR = "未知错误";
    public static final String USER_NOT_LOGIN = "用户未登录";
    public static final String CATEGORY_BE_RELATED_BY_SETMEAL = "当前分类关联了套餐,不能删除";
    public static final String CATEGORY_BE_RELATED_BY_DISH = "当前分类关联了菜品,不能删除";
    public static final String SHOPPING_CART_IS_NULL = "购物车数据为空，不能下单";
    public static final String ADDRESS_BOOK_IS_NULL = "用户地址为空，不能下单";
    public static final String LOGIN_FAILED = "登录失败";
    public static final String UPLOAD_FAILED = "文件上传失败";
    public static final String SETMEAL_ENABLE_FAILED = "套餐内包含未启售菜品，无法启售";
    public static final String PASSWORD_EDIT_FAILED = "密码修改失败";
    public static final String DISH_ON_SALE = "起售中的菜品不能删除";
    public static final String SETMEAL_ON_SALE = "起售中的套餐不能删除";
    public static final String DISH_BE_RELATED_BY_SETMEAL = "当前菜品关联了套餐,不能删除";
    public static final String ORDER_STATUS_ERROR = "订单状态错误";
    public static final String ORDER_NOT_FOUND = "订单不存在";

    /* ---------------- 成员留言 ---------------- */
    public static final String MESSAGE_PLAYER_ID_EMPTY = "玩家ID不能为空";
    public static final String MESSAGE_PLAYER_ID_TOO_LONG = "玩家ID不能超过64个字符";
    public static final String MESSAGE_QQ_TOO_LONG = "QQ号不能超过32个字符";
    public static final String MESSAGE_CONTENT_EMPTY = "留言内容不能为空";
    public static final String MESSAGE_CONTENT_TOO_LONG = "留言内容不能超过500个字";
    public static final String MESSAGE_DUPLICATE_SUBMIT = "和上一条留言内容相同，请勿重复提交";
    public static final String MESSAGE_PLAYER_NOT_BOUND = "未找到该QQ号对应的玩家，首次发布请带上 playerId";
    public static final String MESSAGE_API_KEY_NOT_CONFIGURED = "留言发布接口密钥未配置，请先在 config 表中添加 message_api_key";
    public static final String MESSAGE_API_KEY_INVALID = "接口密钥校验失败";
    public static final String MESSAGE_NOT_FOUND = "留言不存在";

    /* ---------------- 展览内容（机器 / 建筑 / 其他） ---------------- */
    public static final String EXHIBITION_TITLE_EMPTY = "标题不能为空";
    public static final String EXHIBITION_TITLE_TOO_LONG = "标题不能超过128个字符";
    public static final String EXHIBITION_CATEGORY_EMPTY = "请选择分类";
    public static final String EXHIBITION_CATEGORY_INVALID = "分类不合法，只能是红石 / 建筑 / 其他";
    public static final String EXHIBITION_IMAGE_EMPTY = "请至少上传一张图片";
    public static final String EXHIBITION_IMAGE_TOO_MANY = "图片数量不能超过30张";
    public static final String EXHIBITION_IMAGE_URL_TOO_LONG = "图片地址过长，请重新上传";
    public static final String EXHIBITION_CONTENT_EMPTY = "正文不能为空";
    public static final String EXHIBITION_CONTENT_TOO_LONG = "正文不能超过5000个字";
    public static final String EXHIBITION_NOT_FOUND = "展览内容不存在";

}
