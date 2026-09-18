package com.sct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sct.constant.MessageConstant;
import com.sct.context.BaseContext;
import com.sct.dto.MemberMessagePageQueryDTO;
import com.sct.dto.MemberMessagePublishDTO;
import com.sct.dto.MemberMessageSaveDTO;
import com.sct.dto.MemberMessageUpdateDTO;
import com.sct.entity.Config;
import com.sct.entity.MemberMessage;
import com.sct.exception.BaseException;
import com.sct.mapper.ConfigMapper;
import com.sct.mapper.MemberMessageMapper;
import com.sct.result.PageResult;
import com.sct.service.MemberMessageService;
import com.sct.vo.MemberMessageAdminVO;
import com.sct.vo.MemberMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title: MemberMessageServiceImpl
 * @Author Xc_Star
 * @Package com.sct.service.impl
 * @Date 2026/9/18 10:20
 */

@Slf4j
@Service
public class MemberMessageServiceImpl implements MemberMessageService {

    /** 机器人接口密钥在 config 表中的 key */
    private static final String CONFIG_KEY_API_KEY = "message_api_key";

    /** 头像服务地址，playerId 直接拼在后面 */
    private static final String AVATAR_URL_PREFIX = "https://crafthead.net/avatar/";

    /** 单条留言最大长度，和 member_message.content 字段保持一致 */
    private static final int CONTENT_MAX_LENGTH = 500;

    /** 玩家ID最大长度，和 member_message.player_id 字段保持一致 */
    private static final int PLAYER_ID_MAX_LENGTH = 64;

    /** QQ号最大长度，和 member_message.qq 字段保持一致 */
    private static final int QQ_MAX_LENGTH = 32;

    /** 列表接口一次最多返回多少条 */
    private static final int LIST_MAX_LIMIT = 200;

    /** 同一玩家在多少秒内提交完全相同的内容视为重复提交 */
    private static final int DUPLICATE_WINDOW_SECONDS = 60;

    /** 机器人写入的留言统一打上这个备注，方便区分来源 */
    private static final String REMARK_FROM_API = "api";

    /** 后台新增留言时取不到操作人，用这个备注兜底 */
    private static final String REMARK_FROM_ADMIN = "admin";

    @Resource
    private MemberMessageMapper memberMessageMapper;

    @Resource
    private ConfigMapper configMapper;

    @Override
    public List<MemberMessageVO> list(Integer limit) {
        LambdaQueryWrapper<MemberMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberMessage::getStatus, MemberMessage.STATUS_NORMAL)
                .orderByDesc(MemberMessage::getTop)
                .orderByDesc(MemberMessage::getCreateTime)
                .orderByDesc(MemberMessage::getId);
        if (limit != null && limit > 0) {
            // limit 是 Integer，不会造成 SQL 注入
            wrapper.last("LIMIT " + Math.min(limit, LIST_MAX_LIMIT));
        }
        return memberMessageMapper.selectList(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publish(MemberMessagePublishDTO dto) {
        if (dto == null) {
            throw new BaseException(MessageConstant.MESSAGE_CONTENT_EMPTY);
        }

        String content = checkContent(dto.getContent());

        String qq = trimToNull(dto.getQq());
        String playerId = trimToNull(dto.getPlayerId());
        if (playerId == null) {
            // 机器人只拿得到 QQ 号时，用该 QQ 最近一次发布用过的玩家ID兜底
            if (qq == null) {
                throw new BaseException(MessageConstant.MESSAGE_PLAYER_ID_EMPTY);
            }
            playerId = findLastPlayerIdByQq(qq);
            if (playerId == null) {
                throw new BaseException(MessageConstant.MESSAGE_PLAYER_NOT_BOUND);
            }
        }

        if (isDuplicate(playerId, content)) {
            throw new BaseException(MessageConstant.MESSAGE_DUPLICATE_SUBMIT);
        }

        MemberMessage message = MemberMessage.builder()
                .playerId(playerId)
                .qq(qq)
                .content(content)
                .top(MemberMessage.TOP_NO)
                .status(MemberMessage.STATUS_NORMAL)
                .build();
        message.setRemark(REMARK_FROM_API);
        // 数据库服务器的时区不一定和 JVM 一致，时间统一由应用侧写入，避免排序和重复校验出现偏差
        LocalDateTime now = LocalDateTime.now();
        message.setCreateTime(now);
        message.setUpdateTime(now);
        memberMessageMapper.insert(message);
        log.info("收到成员留言：playerId={}, qq={}, id={}", playerId, qq, message.getId());
        return message.getId();
    }

    @Override
    public void checkApiKey(String apiKey) {
        String expected = getConfigValue(CONFIG_KEY_API_KEY);
        if (expected == null) {
            log.warn("config 表中未配置 {}，留言发布接口不可用", CONFIG_KEY_API_KEY);
            throw new BaseException(MessageConstant.MESSAGE_API_KEY_NOT_CONFIGURED);
        }
        if (apiKey == null || !constantTimeEquals(expected, apiKey.trim())) {
            throw new BaseException(MessageConstant.MESSAGE_API_KEY_INVALID);
        }
    }

    @Override
    public PageResult<MemberMessageAdminVO> pageQuery(MemberMessagePageQueryDTO dto) {
        MemberMessagePageQueryDTO query = dto == null ? new MemberMessagePageQueryDTO() : dto;
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        try {
            LambdaQueryWrapper<MemberMessage> wrapper = new LambdaQueryWrapper<>();
            if (query.getStatus() != null) {
                wrapper.eq(MemberMessage::getStatus, query.getStatus());
            }
            String keyword = trimToNull(query.getKeyword());
            if (keyword != null) {
                wrapper.and(w -> w.like(MemberMessage::getPlayerId, keyword)
                        .or().like(MemberMessage::getContent, keyword)
                        .or().like(MemberMessage::getQq, keyword));
            }
            wrapper.orderByDesc(MemberMessage::getTop)
                    .orderByDesc(MemberMessage::getId);

            List<MemberMessage> messages = memberMessageMapper.selectList(wrapper);
            PageInfo<MemberMessage> pageInfo = new PageInfo<>(messages);
            List<MemberMessageAdminVO> list = pageInfo.getList().stream()
                    .map(this::toAdminVO)
                    .collect(Collectors.toList());
            return new PageResult<>(list, pageInfo.getTotal());
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public void update(MemberMessageUpdateDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new BaseException(MessageConstant.MESSAGE_NOT_FOUND);
        }
        MemberMessage message = memberMessageMapper.selectById(dto.getId());
        if (message == null) {
            throw new BaseException(MessageConstant.MESSAGE_NOT_FOUND);
        }
        if (dto.getTop() != null) {
            message.setTop(MemberMessage.TOP_YES.equals(dto.getTop()) ? MemberMessage.TOP_YES : MemberMessage.TOP_NO);
        }
        if (dto.getStatus() != null) {
            message.setStatus(MemberMessage.STATUS_NORMAL.equals(dto.getStatus())
                    ? MemberMessage.STATUS_NORMAL : MemberMessage.STATUS_HIDDEN);
        }
        message.setUpdateTime(LocalDateTime.now());
        memberMessageMapper.updateById(message);
        log.info("{} 修改了留言 id={}：top={}, status={}",
                BaseContext.getUsername(), message.getId(), message.getTop(), message.getStatus());
    }

    @Override
    public void remove(Long id) {
        if (id == null) {
            throw new BaseException(MessageConstant.MESSAGE_NOT_FOUND);
        }
        MemberMessage message = memberMessageMapper.selectById(id);
        if (message == null) {
            throw new BaseException(MessageConstant.MESSAGE_NOT_FOUND);
        }
        // @TableLogic，实际是逻辑删除
        memberMessageMapper.deleteById(id);
        log.info("{} 删除了留言 id={}（playerId={}）", BaseContext.getUsername(), id, message.getPlayerId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(MemberMessageSaveDTO dto) {
        if (dto == null) {
            throw new BaseException(MessageConstant.MESSAGE_CONTENT_EMPTY);
        }

        String content = checkContent(dto.getContent());

        String playerId = trimToNull(dto.getPlayerId());
        if (playerId == null) {
            throw new BaseException(MessageConstant.MESSAGE_PLAYER_ID_EMPTY);
        }
        if (playerId.length() > PLAYER_ID_MAX_LENGTH) {
            throw new BaseException(MessageConstant.MESSAGE_PLAYER_ID_TOO_LONG);
        }

        String qq = trimToNull(dto.getQq());
        if (qq != null && qq.length() > QQ_MAX_LENGTH) {
            throw new BaseException(MessageConstant.MESSAGE_QQ_TOO_LONG);
        }

        MemberMessage message = MemberMessage.builder()
                .playerId(playerId)
                .qq(qq)
                .content(content)
                .top(MemberMessage.TOP_YES.equals(dto.getTop()) ? MemberMessage.TOP_YES : MemberMessage.TOP_NO)
                .status(MemberMessage.STATUS_HIDDEN.equals(dto.getStatus())
                        ? MemberMessage.STATUS_HIDDEN : MemberMessage.STATUS_NORMAL)
                .build();
        String operator = BaseContext.getUsername();
        message.setRemark(operator == null ? REMARK_FROM_ADMIN : operator);
        LocalDateTime now = LocalDateTime.now();
        message.setCreateTime(now);
        message.setUpdateTime(now);
        memberMessageMapper.insert(message);
        log.info("{} 后台新增留言：playerId={}, qq={}, id={}", operator, playerId, qq, message.getId());
        return message.getId();
    }

    /**
     * 校验留言内容并返回去掉首尾空白后的结果
     */
    private String checkContent(String rawContent) {
        String content = trimToNull(rawContent);
        if (content == null) {
            throw new BaseException(MessageConstant.MESSAGE_CONTENT_EMPTY);
        }
        if (content.length() > CONTENT_MAX_LENGTH) {
            throw new BaseException(MessageConstant.MESSAGE_CONTENT_TOO_LONG);
        }
        return content;
    }

    /**
     * 按 QQ 号查最近一次发布用过的玩家ID
     */
    private String findLastPlayerIdByQq(String qq) {
        LambdaQueryWrapper<MemberMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberMessage::getQq, qq)
                .isNotNull(MemberMessage::getPlayerId)
                .ne(MemberMessage::getPlayerId, "")
                .orderByDesc(MemberMessage::getId)
                .last("LIMIT 1");
        MemberMessage last = memberMessageMapper.selectOne(wrapper);
        return last == null ? null : last.getPlayerId();
    }

    /**
     * 判断是否在短时间内重复提交了完全相同的内容
     */
    private boolean isDuplicate(String playerId, String content) {
        LambdaQueryWrapper<MemberMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberMessage::getPlayerId, playerId)
                .eq(MemberMessage::getContent, content)
                .orderByDesc(MemberMessage::getId)
                .last("LIMIT 1");
        MemberMessage last = memberMessageMapper.selectOne(wrapper);
        if (last == null || last.getCreateTime() == null) {
            return false;
        }
        long seconds = Duration.between(last.getCreateTime(), LocalDateTime.now()).getSeconds();
        return seconds >= 0 && seconds < DUPLICATE_WINDOW_SECONDS;
    }

    private MemberMessageVO toVO(MemberMessage message) {
        return MemberMessageVO.builder()
                .id(message.getId())
                .playerId(message.getPlayerId())
                .avatar(AVATAR_URL_PREFIX + message.getPlayerId())
                .content(message.getContent())
                .top(MemberMessage.TOP_YES.equals(message.getTop()))
                .createTime(message.getCreateTime())
                .build();
    }

    private MemberMessageAdminVO toAdminVO(MemberMessage message) {
        return MemberMessageAdminVO.builder()
                .id(message.getId())
                .playerId(message.getPlayerId())
                .avatar(AVATAR_URL_PREFIX + message.getPlayerId())
                .qq(message.getQq())
                .content(message.getContent())
                .top(message.getTop())
                .status(message.getStatus())
                .createTime(message.getCreateTime())
                .remark(message.getRemark())
                .build();
    }

    private String getConfigValue(String configKey) {
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigKey, configKey).last("LIMIT 1");
        Config config = configMapper.selectOne(wrapper);
        return config == null ? null : trimToNull(config.getConfigValue());
    }

    /**
     * 定长比较，避免通过响应时间猜密钥
     */
    private boolean constantTimeEquals(String expected, String actual) {
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
