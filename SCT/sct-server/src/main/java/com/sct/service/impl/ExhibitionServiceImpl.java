package com.sct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sct.constant.MessageConstant;
import com.sct.context.BaseContext;
import com.sct.dto.ExhibitionPageQueryDTO;
import com.sct.dto.ExhibitionSaveDTO;
import com.sct.dto.ExhibitionUpdateDTO;
import com.sct.entity.Exhibition;
import com.sct.exception.BaseException;
import com.sct.mapper.ExhibitionMapper;
import com.sct.result.PageResult;
import com.sct.service.ExhibitionService;
import com.sct.vo.ExhibitionAdminVO;
import com.sct.vo.ExhibitionDetailVO;
import com.sct.vo.ExhibitionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title: ExhibitionServiceImpl
 * @Author Xc_Star
 * @Package com.sct.service.impl
 * @Date 2026/9/18 15:40
 */

@Slf4j
@Service
public class ExhibitionServiceImpl implements ExhibitionService {

    /** 标题最大长度，和 exhibition.title 字段保持一致 */
    private static final int TITLE_MAX_LENGTH = 128;

    /** 正文最大长度，exhibition.content 是 TEXT，这里做业务层兜底 */
    private static final int CONTENT_MAX_LENGTH = 5000;

    /** 单条展览最多允许的图片数量 */
    private static final int IMAGE_MAX_COUNT = 30;

    /** 图片地址最大长度，和 exhibition.cover 字段保持一致 */
    private static final int IMAGE_URL_MAX_LENGTH = 512;

    /** 列表接口一次最多返回多少条 */
    private static final int LIST_MAX_LIMIT = 200;

    /** 后台新增时取不到操作人，用这个备注兜底 */
    private static final String REMARK_FROM_ADMIN = "admin";

    @Resource
    private ExhibitionMapper exhibitionMapper;

    @Override
    public List<ExhibitionVO> list(String category, Integer limit) {
        LambdaQueryWrapper<Exhibition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Exhibition::getStatus, Exhibition.STATUS_NORMAL);
        String trimmedCategory = trimToNull(category);
        if (trimmedCategory != null) {
            checkCategory(trimmedCategory);
            wrapper.eq(Exhibition::getCategory, trimmedCategory);
        }
        wrapper.orderByDesc(Exhibition::getTop)
                .orderByDesc(Exhibition::getCreateTime)
                .orderByDesc(Exhibition::getId);
        if (limit != null && limit > 0) {
            // limit 是 Integer，不会造成 SQL 注入
            wrapper.last("LIMIT " + Math.min(limit, LIST_MAX_LIMIT));
        }
        return exhibitionMapper.selectList(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public ExhibitionDetailVO detail(Long id) {
        Exhibition exhibition = getVisibleOrThrow(id);
        return toDetailVO(exhibition);
    }

    @Override
    public PageResult<ExhibitionAdminVO> pageQuery(ExhibitionPageQueryDTO dto) {
        ExhibitionPageQueryDTO query = dto == null ? new ExhibitionPageQueryDTO() : dto;
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        try {
            LambdaQueryWrapper<Exhibition> wrapper = new LambdaQueryWrapper<>();
            String category = trimToNull(query.getCategory());
            if (category != null) {
                checkCategory(category);
                wrapper.eq(Exhibition::getCategory, category);
            }
            if (query.getStatus() != null) {
                wrapper.eq(Exhibition::getStatus, query.getStatus());
            }
            String keyword = trimToNull(query.getKeyword());
            if (keyword != null) {
                wrapper.and(w -> w.like(Exhibition::getTitle, keyword)
                        .or().like(Exhibition::getContent, keyword));
            }
            wrapper.orderByDesc(Exhibition::getTop)
                    .orderByDesc(Exhibition::getId);

            List<Exhibition> records = exhibitionMapper.selectList(wrapper);
            PageInfo<Exhibition> pageInfo = new PageInfo<>(records);
            List<ExhibitionAdminVO> list = pageInfo.getList().stream()
                    .map(this::toAdminVO)
                    .collect(Collectors.toList());
            return new PageResult<>(list, pageInfo.getTotal());
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(ExhibitionSaveDTO dto) {
        if (dto == null) {
            throw new BaseException(MessageConstant.EXHIBITION_TITLE_EMPTY);
        }
        String title = checkTitle(dto.getTitle());
        String category = checkCategory(dto.getCategory());
        List<String> images = checkImages(dto.getImages());
        String content = checkContent(dto.getContent());

        Exhibition exhibition = Exhibition.builder()
                .title(title)
                .category(category)
                .cover(resolveCover(dto.getCover(), images))
                .images(images)
                .content(content)
                .top(Exhibition.TOP_YES.equals(dto.getTop()) ? Exhibition.TOP_YES : Exhibition.TOP_NO)
                .status(Exhibition.STATUS_HIDDEN.equals(dto.getStatus())
                        ? Exhibition.STATUS_HIDDEN : Exhibition.STATUS_NORMAL)
                .build();
        String operator = BaseContext.getUsername();
        exhibition.setRemark(operator == null ? REMARK_FROM_ADMIN : operator);
        // 数据库服务器时区不一定和 JVM 一致，时间统一由应用侧写入
        LocalDateTime now = LocalDateTime.now();
        exhibition.setCreateTime(now);
        exhibition.setUpdateTime(now);
        exhibitionMapper.insert(exhibition);
        log.info("{} 新增展览内容：category={}, title={}, id={}", operator, category, title, exhibition.getId());
        return exhibition.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ExhibitionUpdateDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new BaseException(MessageConstant.EXHIBITION_NOT_FOUND);
        }
        Exhibition exhibition = exhibitionMapper.selectById(dto.getId());
        if (exhibition == null) {
            throw new BaseException(MessageConstant.EXHIBITION_NOT_FOUND);
        }

        if (dto.getTitle() != null) {
            exhibition.setTitle(checkTitle(dto.getTitle()));
        }
        if (dto.getCategory() != null) {
            exhibition.setCategory(checkCategory(dto.getCategory()));
        }
        if (dto.getContent() != null) {
            exhibition.setContent(checkContent(dto.getContent()));
        }
        if (dto.getImages() != null) {
            List<String> images = checkImages(dto.getImages());
            exhibition.setImages(images);
            // 图片换了但没单独传封面时，封面跟着走
            if (dto.getCover() == null) {
                exhibition.setCover(images.get(0));
            }
        }
        if (dto.getCover() != null) {
            exhibition.setCover(resolveCover(dto.getCover(), exhibition.getImages()));
        }
        if (dto.getTop() != null) {
            exhibition.setTop(Exhibition.TOP_YES.equals(dto.getTop()) ? Exhibition.TOP_YES : Exhibition.TOP_NO);
        }
        if (dto.getStatus() != null) {
            exhibition.setStatus(Exhibition.STATUS_NORMAL.equals(dto.getStatus())
                    ? Exhibition.STATUS_NORMAL : Exhibition.STATUS_HIDDEN);
        }
        // 封面始终要有值，兜底取第一张图
        if (trimToNull(exhibition.getCover()) == null
                && exhibition.getImages() != null && !exhibition.getImages().isEmpty()) {
            exhibition.setCover(exhibition.getImages().get(0));
        }
        exhibition.setUpdateTime(LocalDateTime.now());
        exhibitionMapper.updateById(exhibition);
        log.info("{} 修改了展览内容 id={}：category={}, title={}, top={}, status={}",
                BaseContext.getUsername(), exhibition.getId(), exhibition.getCategory(),
                exhibition.getTitle(), exhibition.getTop(), exhibition.getStatus());
    }

    @Override
    public void remove(Long id) {
        if (id == null) {
            throw new BaseException(MessageConstant.EXHIBITION_NOT_FOUND);
        }
        Exhibition exhibition = exhibitionMapper.selectById(id);
        if (exhibition == null) {
            throw new BaseException(MessageConstant.EXHIBITION_NOT_FOUND);
        }
        // @TableLogic，实际是逻辑删除
        exhibitionMapper.deleteById(id);
        log.info("{} 删除了展览内容 id={}（title={}）",
                BaseContext.getUsername(), id, exhibition.getTitle());
    }

    /**
     * 按 id 取一条对外可见的展览内容，不存在或已隐藏都按「不存在」处理
     */
    private Exhibition getVisibleOrThrow(Long id) {
        if (id == null) {
            throw new BaseException(MessageConstant.EXHIBITION_NOT_FOUND);
        }
        Exhibition exhibition = exhibitionMapper.selectById(id);
        if (exhibition == null || !Exhibition.STATUS_NORMAL.equals(exhibition.getStatus())) {
            throw new BaseException(MessageConstant.EXHIBITION_NOT_FOUND);
        }
        return exhibition;
    }

    private String checkTitle(String rawTitle) {
        String title = trimToNull(rawTitle);
        if (title == null) {
            throw new BaseException(MessageConstant.EXHIBITION_TITLE_EMPTY);
        }
        if (title.length() > TITLE_MAX_LENGTH) {
            throw new BaseException(MessageConstant.EXHIBITION_TITLE_TOO_LONG);
        }
        return title;
    }

    private String checkCategory(String rawCategory) {
        String category = trimToNull(rawCategory);
        if (category == null) {
            throw new BaseException(MessageConstant.EXHIBITION_CATEGORY_EMPTY);
        }
        if (!Exhibition.isSupportedCategory(category)) {
            throw new BaseException(MessageConstant.EXHIBITION_CATEGORY_INVALID);
        }
        return category;
    }

    private String checkContent(String rawContent) {
        String content = trimToNull(rawContent);
        if (content == null) {
            throw new BaseException(MessageConstant.EXHIBITION_CONTENT_EMPTY);
        }
        if (content.length() > CONTENT_MAX_LENGTH) {
            throw new BaseException(MessageConstant.EXHIBITION_CONTENT_TOO_LONG);
        }
        return content;
    }

    /**
     * 校验图片列表：去掉空串、限制数量与单条地址长度
     */
    private List<String> checkImages(List<String> rawImages) {
        if (rawImages == null || rawImages.isEmpty()) {
            throw new BaseException(MessageConstant.EXHIBITION_IMAGE_EMPTY);
        }
        List<String> images = rawImages.stream()
                .map(this::trimToNull)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
        if (images.isEmpty()) {
            throw new BaseException(MessageConstant.EXHIBITION_IMAGE_EMPTY);
        }
        if (images.size() > IMAGE_MAX_COUNT) {
            throw new BaseException(MessageConstant.EXHIBITION_IMAGE_TOO_MANY);
        }
        for (String image : images) {
            if (image.length() > IMAGE_URL_MAX_LENGTH) {
                throw new BaseException(MessageConstant.EXHIBITION_IMAGE_URL_TOO_LONG);
            }
        }
        return images;
    }

    /**
     * 封面为空时取图片列表的第一张
     */
    private String resolveCover(String rawCover, List<String> images) {
        String cover = trimToNull(rawCover);
        if (cover != null) {
            return cover;
        }
        return images == null || images.isEmpty() ? null : images.get(0);
    }

    private ExhibitionVO toVO(Exhibition exhibition) {
        return ExhibitionVO.builder()
                .id(exhibition.getId())
                .title(exhibition.getTitle())
                .category(exhibition.getCategory())
                // 历史数据或直接写库的数据可能没存封面，读取时再兜底一次
                .cover(resolveCover(exhibition.getCover(), exhibition.getImages()))
                .imageCount(imageCount(exhibition))
                .top(Exhibition.TOP_YES.equals(exhibition.getTop()))
                .createTime(exhibition.getCreateTime())
                .build();
    }

    private ExhibitionDetailVO toDetailVO(Exhibition exhibition) {
        return ExhibitionDetailVO.builder()
                .id(exhibition.getId())
                .title(exhibition.getTitle())
                .category(exhibition.getCategory())
                .cover(resolveCover(exhibition.getCover(), exhibition.getImages()))
                .images(imageList(exhibition))
                .content(exhibition.getContent())
                .top(Exhibition.TOP_YES.equals(exhibition.getTop()))
                .createTime(exhibition.getCreateTime())
                .build();
    }

    private ExhibitionAdminVO toAdminVO(Exhibition exhibition) {
        return ExhibitionAdminVO.builder()
                .id(exhibition.getId())
                .title(exhibition.getTitle())
                .category(exhibition.getCategory())
                .cover(exhibition.getCover())
                .images(imageList(exhibition))
                .content(exhibition.getContent())
                .top(exhibition.getTop())
                .status(exhibition.getStatus())
                .createTime(exhibition.getCreateTime())
                .remark(exhibition.getRemark())
                .build();
    }

    private List<String> imageList(Exhibition exhibition) {
        List<String> images = exhibition.getImages();
        return images == null ? Collections.emptyList() : new ArrayList<>(images);
    }

    private Integer imageCount(Exhibition exhibition) {
        return exhibition.getImages() == null ? 0 : exhibition.getImages().size();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
