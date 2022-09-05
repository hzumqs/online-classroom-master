package com.service_live.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.entity.model.live.LiveCourse;
import com.entity.vo.live.LiveCourseVo;

import java.util.List;

/**
 * <p>
 * 直播课程表 Mapper 接口
 * </p>
 *
 * @author cc
 * @since 2022-07-20
 */
public interface LiveCourseMapper extends BaseMapper<LiveCourse> {
    //获取最近直播
    List<LiveCourseVo> findLatelyList();
}
