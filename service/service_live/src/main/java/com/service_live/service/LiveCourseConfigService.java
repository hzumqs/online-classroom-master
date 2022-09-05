package com.service_live.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.model.live.LiveCourseConfig;

/**
 * <p>
 * 直播课程配置表 服务类
 * </p>
 *
 * @author cc
 * @since 2022-07-20
 */
public interface LiveCourseConfigService extends IService<LiveCourseConfig> {

    LiveCourseConfig getByLiveCourseId(Long id);
}
