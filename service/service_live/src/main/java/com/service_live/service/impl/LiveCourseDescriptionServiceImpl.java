package com.service_live.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entity.model.live.LiveCourseDescription;
import com.service_live.mapper.LiveCourseDescriptionMapper;
import com.service_live.service.LiveCourseDescriptionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程简介 服务实现类
 * </p>
 *
 * @author cc
 * @since 2022-07-20
 */
@Service
public class LiveCourseDescriptionServiceImpl extends ServiceImpl<LiveCourseDescriptionMapper, LiveCourseDescription> implements LiveCourseDescriptionService {

    @Override
    public LiveCourseDescription getByLiveCourseId(Long liveCourseId) {
        return this.getOne(new LambdaQueryWrapper<LiveCourseDescription>().eq(LiveCourseDescription::getLiveCourseId, liveCourseId));
    }
}
