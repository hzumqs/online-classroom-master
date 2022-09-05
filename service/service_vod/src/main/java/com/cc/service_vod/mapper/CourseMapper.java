package com.cc.service_vod.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.entity.model.vod.Course;
import com.entity.vo.vod.CoursePublishVo;
import com.entity.vo.vod.CourseVo;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author cc
 * @since 2022-07-11
 */
public interface CourseMapper extends BaseMapper<Course> {

    CoursePublishVo selectCoursePublishVoById(Long id);

    CourseVo selectCourseVoById(Long id);
}
