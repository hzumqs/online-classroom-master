package com.service_live.service;

import com.entity.model.live.LiveCourseDescription;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程简介 服务类
 * </p>
 *
 * @author cc
 * @since 2022-07-20
 */
public interface LiveCourseDescriptionService extends IService<LiveCourseDescription> {
    LiveCourseDescription getByLiveCourseId(Long liveCourseId);
}