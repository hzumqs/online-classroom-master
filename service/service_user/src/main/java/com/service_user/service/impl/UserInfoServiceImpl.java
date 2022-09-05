package com.service_user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.entity.model.user.UserInfo;
import com.service_user.mapper.UserInfoMapper;
import com.service_user.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author cc
 * @since 2022-07-15
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Override
    public UserInfo getByOpenid(String openId) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("open_id",openId);
        UserInfo userInfo = baseMapper.selectOne(wrapper);
        return userInfo;
    }
}
