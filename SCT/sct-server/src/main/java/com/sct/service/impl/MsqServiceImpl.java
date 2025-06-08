package com.sct.service.impl;

import com.sct.entity.Msq;
import com.sct.mapper.MsqMapper;
import com.sct.service.MsqService;
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
}
