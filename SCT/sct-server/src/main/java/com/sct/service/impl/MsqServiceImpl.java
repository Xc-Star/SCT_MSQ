package com.sct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sct.dto.MsqResultPageDTO;
import com.sct.dto.MsqResultUpdateStatusDTO;
import com.sct.entity.Msq;
import com.sct.entity.MsqResult;
import com.sct.mapper.MsqMapper;
import com.sct.mapper.MsqResultMapper;
import com.sct.result.PageResult;
import com.sct.service.MsqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Title: MsqServiceImpl
 * @Author Xc_Star
 * @Package com.sct.service.impl
 * @Date 2025/6/6 22:32
 */

@Service
public class MsqServiceImpl implements MsqService {

    @Resource
    private MsqMapper msqMapper;
    @Autowired
    private MsqResultMapper msqResultMapper;

    /**
     * 获取所有问卷
     * @return 问卷列表
     */
    public List<Msq> list() {
        return msqMapper.selectList(null);
    }

    /**
     * 保存问卷
     * @param msq 问卷
     */
    @Override
    public void saveMsq(Msq msq) {
        msqMapper.insert(msq);
    }

    /**
     * 更新问卷
     * @param msq 问卷
     */
    @Override
    public void updateMsq(Msq msq) {
        msqMapper.updateById(msq);
    }

    /**
     * 更新问卷状态
     * @param id 问卷id
     * @param status 状态
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        msqMapper.updateById(Msq.builder().id(id).status(status).build());
    }

    /**
     * 根据id查询问卷
     * @param id 问卷id
     * @return 问卷
     */
    @Override
    public Msq getById(Long id) {
        return msqMapper.selectById(id);
    }

    /**
     * 删除问卷
     * @param id 问卷id
     */
    @Override
    public void deleteById(Long id) {
        msqMapper.deleteById(id);
    }

    @Override
    public List<Msq> getTypeMsq(Integer type) {
        return msqMapper.selectList(new QueryWrapper<Msq>().eq("type", type));
    }

    @Override
    public PageResult<MsqResult> getResultPage(MsqResultPageDTO msqResultPageDTO) {
        // 构建查询条件
        QueryWrapper<MsqResult> queryWrapper = new QueryWrapper<MsqResult>()
                .orderByDesc("create_time");

        // 只有当respondent不为null时才添加查询条件
        if (msqResultPageDTO.getRespondent() != null) {
            queryWrapper.like("respondent", msqResultPageDTO.getRespondent());
        }

        // 只有当respondent_contact不为null时才添加查询条件
        if (msqResultPageDTO.getRespondentContact() != null) {
            queryWrapper.like("respondent_contact", msqResultPageDTO.getRespondentContact());
        }

        if (msqResultPageDTO.getType() != null) {
            queryWrapper.eq("type", msqResultPageDTO.getType());
        }

        // 执行分页查询
        PageHelper.startPage(msqResultPageDTO.getPageNo(), msqResultPageDTO.getPageSize());
        List<MsqResult> msqResults = msqResultMapper.selectList(queryWrapper);

        // 获取分页信息
        PageInfo<MsqResult> pageInfo = new PageInfo<>(msqResults);

        // 返回包含实际数据的PageResult
        return new PageResult<>(pageInfo.getList(), pageInfo.getTotal());
    }

    @Override
    public void updateMsqResultStatus(MsqResultUpdateStatusDTO msqResultUpdateStatusDTO) {
        msqResultMapper.updateById(MsqResult.builder()
                .id(msqResultUpdateStatusDTO.getId())
                .status(msqResultUpdateStatusDTO.getStatus())
                .build());
    }
}
