package com.service_live.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.model.live.LiveCourseAccount;

/**
 * <p>
 * 直播课程账号表（受保护信息） 服务类
 * </p>
 *
 * @author cc
 * @since 2022-07-20
 */
public interface LiveCourseAccountService extends IService<LiveCourseAccount> {
    LiveCourseAccount getByLiveCourseId(Long liveCourseId);

}
