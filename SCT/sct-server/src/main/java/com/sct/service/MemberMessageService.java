package com.sct.service;

import com.sct.dto.MemberMessagePageQueryDTO;
import com.sct.dto.MemberMessagePublishDTO;
import com.sct.dto.MemberMessageSaveDTO;
import com.sct.dto.MemberMessageUpdateDTO;
import com.sct.result.PageResult;
import com.sct.vo.MemberMessageAdminVO;
import com.sct.vo.MemberMessageVO;

import java.util.List;

/**
 * @Title: MemberMessageService
 * @Author Xc_Star
 * @Package com.sct.service
 * @Date 2026/9/18 10:20
 */
public interface MemberMessageService {

    /**
     * 查询对外展示的留言列表（置顶优先，其次按时间倒序）
     *
     * @param limit 返回条数，为空或小于等于 0 时返回全部
     * @return 留言列表
     */
    List<MemberMessageVO> list(Integer limit);

    /**
     * 发布一条留言（供 QQ 机器人插件调用）
     *
     * @param dto 留言内容
     * @return 新留言的 id
     */
    Long publish(MemberMessagePublishDTO dto);

    /**
     * 校验调用方携带的接口密钥
     *
     * @param apiKey 请求头中的密钥
     */
    void checkApiKey(String apiKey);

    /**
     * 后台手动新增一条留言
     *
     * @param dto 留言内容
     * @return 新留言的 id
     */
    Long save(MemberMessageSaveDTO dto);

    /**
     * 后台分页查询留言
     *
     * @param dto 分页与筛选条件
     * @return 分页结果
     */
    PageResult<MemberMessageAdminVO> pageQuery(MemberMessagePageQueryDTO dto);

    /**
     * 后台修改留言的置顶 / 显示状态
     *
     * @param dto id 必填，top 与 status 为 null 表示不修改
     */
    void update(MemberMessageUpdateDTO dto);

    /**
     * 后台删除留言（逻辑删除）
     *
     * @param id 留言id
     */
    void remove(Long id);
}
