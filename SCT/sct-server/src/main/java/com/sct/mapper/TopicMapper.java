package com.sct.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sct.entity.Topic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Title: TopicMapper
 * @Author Xc_Star
 * @Package com.sct.mapper
 * @Date 2025/6/6 22:33
 */

@Mapper
public interface TopicMapper extends BaseMapper<Topic> {

    @Select("select * from topic where id < 0")
    List<Topic> getTopicParent();
}
