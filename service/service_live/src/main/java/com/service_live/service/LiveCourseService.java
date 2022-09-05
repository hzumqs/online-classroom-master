package com.service_live.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entity.model.live.LiveCourse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.vo.live.LiveCourseConfigVo;
import com.entity.vo.live.LiveCourseFormVo;
import com.entity.vo.live.LiveCourseVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 直播课程表 服务类
 * </p>
 *
 * @author cc
 * @since 2022-07-20
 */
public interface LiveCourseService extends IService<LiveCourse> {

    IPage<LiveCourse> selectPage(Page<LiveCourse> pageParam);

    Boolean save(LiveCourseFormVo liveCourseVo);

    //修改
    void updateLiveById(LiveCourseFormVo liveCourseVo);

    //获取
    LiveCourseFormVo getLiveCourseFormVo(Long id);

    void removeLive(Long id);

    //获取配置
    LiveCourseConfigVo getCourseConfig(Long id);

    //修改配置
    void updateConfig(LiveCourseConfigVo liveCourseConfigVo);

    //获取最近的直播
    List<LiveCourseVo> findLatelyList();

    JSONObject getPlayAuth(Long id, Long userId);

    Map<String, Object> getInfoById(Long courseId);
}
