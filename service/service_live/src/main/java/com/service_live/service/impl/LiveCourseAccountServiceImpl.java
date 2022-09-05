package com.service_live.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entity.model.live.LiveCourseAccount;
import com.service_live.mapper.LiveCourseAccountMapper;
import com.service_live.service.LiveCourseAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 直播课程账号表（受保护信息） 服务实现类
 * </p>
 *
 * @author cc
 * @since 2022-07-20
 */
@Service
public class LiveCourseAccountServiceImpl extends ServiceImpl<LiveCourseAccountMapper, LiveCourseAccount> implements LiveCourseAccountService {

    @Override
    public LiveCourseAccount getByLiveCourseId(Long liveCourseId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<LiveCourseAccount>().eq(LiveCourseAccount::getLiveCourseId, liveCourseId));
    }
}
