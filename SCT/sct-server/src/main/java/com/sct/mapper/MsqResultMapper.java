package com.sct.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sct.entity.MsqResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @Title: MsqResultMapper
 * @Author Xc_Star
 * @Package com.sct.mapper
 * @Date 2025/6/6 22:33
 */

@Mapper
public interface MsqResultMapper extends BaseMapper<MsqResult> {

    @Update("update msq_result set reviewer = #{username} where reviewer_id = #{reviewerId}")
    void updateReviewer(Long reviewerId, String username);

    @Update("update msq_result set remover = #{username} where remover_id = #{reviewerId}")
    void updateRemover(Long reviewerId, String username);
}
