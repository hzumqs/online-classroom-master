package com.service_user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.model.user.UserInfo;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author cc
 * @since 2022-07-15
 */
public interface UserInfoService extends IService<UserInfo> {

    UserInfo getByOpenid(String openId);
}
