package com.cc.service_vod.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.model.vod.VideoVisitor;

import java.util.Map;

public interface VideoVisitorService extends IService<VideoVisitor> {
    Map<String, Object> findCount(Long courseId, String startDate, String endDate);
}
