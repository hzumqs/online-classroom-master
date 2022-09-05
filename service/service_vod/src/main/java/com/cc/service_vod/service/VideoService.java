package com.cc.service_vod.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.model.vod.Video;

/**
 * <p>
 * 课程视频 服务类
 * </p>
 *
 * @author cc
 * @since 2022-07-11
 */
public interface VideoService extends IService<Video> {

    void removeVideoByCourseId(Long id);

    void removeVideoById(Long id);
}
