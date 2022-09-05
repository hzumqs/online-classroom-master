package com.cc.service_vod.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.model.vod.Course;
import com.entity.vo.vod.CourseFormVo;
import com.entity.vo.vod.CoursePublishVo;
import com.entity.vo.vod.CourseQueryVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author cc
 * @since 2022-07-11
 */
public interface CourseService extends IService<Course> {

    Map<String, Object> findPage(Page<com.entity.model.vod.Course> pageParam, CourseQueryVo courseQueryVo);

    Long saveCourse(CourseFormVo courseFormVo);

    CourseFormVo getCourseFromVo(Long id);

    void updateCourseById(CourseFormVo courseFormVo);

    CoursePublishVo getCoursePublishVo(Long id);

    int publishCourseById(Long id);

    void removeCourseById(Long id);

    Map<String, Object> getInfoById(Long courseId);

    List<Course> findlist();
}
