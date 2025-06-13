package com.sct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sct.context.BaseContext;
import com.sct.dto.MemberPageQueryDTO;
import com.sct.dto.MemberRemoveDTO;
import com.sct.entity.AdminUser;
import com.sct.entity.MsqResult;
import com.sct.mapper.AdminUserMapper;
import com.sct.mapper.MsqResultMapper;
import com.sct.result.PageResult;
import com.sct.service.MemberService;
import com.sct.utils.BeanUtils;
import com.sct.vo.MemberListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Title: MemberServiceImpl
 * @Author Xc_Star
 * @Package com.sct.service.impl
 * @Date 2025/6/13 01:59
 */

@Service
public class MemberServiceImpl implements MemberService {

    private final String BASE_AVATAR_URL = "https://mc-heads.net/avatar/";

    @Resource
    private MsqResultMapper msqResultMapper;
    @Autowired
    private AdminUserMapper adminUserMapper;

    @Override
    public PageResult<MemberListVO> pageQuery(MemberPageQueryDTO pageParam) {
        PageHelper.startPage(pageParam.getPageNo(), pageParam.getPageSize());

        try {
            QueryWrapper<MsqResult> queryWrapper = new QueryWrapper<MsqResult>();
            if (pageParam.getIsRemoved()) {
                queryWrapper.eq("status", MsqResult.STATUS_REMOVED).orderByDesc("remove_time");;
            } else {
                queryWrapper.eq("status", MsqResult.STATUS_PASS).orderByDesc("review_time");
            }
            List<MsqResult> msqResults = msqResultMapper.selectList(queryWrapper);
            PageInfo<MsqResult> pageInfo = new PageInfo<>(msqResults);

            List<MemberListVO> resultList = new ArrayList<>();
            for (MsqResult msqResult : pageInfo.getList()) {
                MemberListVO bean = BeanUtils.toBean(msqResult, MemberListVO.class);
                bean.setAvatar(BASE_AVATAR_URL + msqResult.getRespondent());
                resultList.add(bean);
            }
            return new PageResult<>(resultList, pageInfo.getTotal());
        } finally {
            PageHelper.clearPage();
        }
    }

    @Transactional
    @Override
    public void updateAdminUser(AdminUser adminUser) {
        msqResultMapper.updateReviewer(adminUser.getId(),  adminUser.getUsername());
        msqResultMapper.updateRemover(adminUser.getId(),  adminUser.getUsername());
    }

    @Override
    public void remove(MemberRemoveDTO memberRemoveDTO) {

        MsqResult msqResult = msqResultMapper.selectById(memberRemoveDTO.getId());
        if (msqResult == null) {
            return;
        }
        AdminUser adminUser = adminUserMapper.selectById(BaseContext.getCurrentId());
        msqResult.setStatus(MsqResult.STATUS_REMOVED);
        msqResult.setRemoverId(adminUser.getId());
        msqResult.setRemover(adminUser.getUsername());
        msqResult.setRemoveTime(LocalDateTime.now());
        msqResult.setRemark(memberRemoveDTO.getRemark());
        msqResultMapper.updateById(msqResult);
    }
}
