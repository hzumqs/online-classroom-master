package com.service_live.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.model.live.LiveCourseGoods;

import java.util.List;

/**
 * <p>
 * 直播课程关联推荐表 服务类
 * </p>
 *
 * @author cc
 * @since 2022-07-20
 */
public interface LiveCourseGoodsService extends IService<LiveCourseGoods> {

    List<LiveCourseGoods> findByLiveCourseId(Long id);


}
