package com.service_user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.courseFeginService.CourseFeignClient;
import com.activityFeginService.ActivityFeignClient;
import com.entity.exception.CustomerException;
import com.entity.model.order.OrderDetail;
import com.entity.model.order.OrderInfo;
import com.entity.model.vod.Course;
import com.entity.result.ResultCodeEnum;
import com.entity.utils.AuthContextHolder;
import com.entity.utils.OrderNoUtils;
import com.entity.vo.order.OrderFormVo;
import com.feginService.FeginUserInfoService;
import com.entity.model.activity.CouponInfo;
import com.entity.model.activity.CouponUse;
import com.entity.model.user.UserInfo;
import com.entity.vo.activity.CouponUseQueryVo;
import com.service_user.mapper.CouponInfoMapper;
import com.service_user.service.CouponInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.service_user.service.CouponUseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 优惠券信息 服务实现类
 * </p>
 *
 * @author cc
 * @since 2022-07-15
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {

    @Autowired
    private FeginUserInfoService feginUserInfoService;


    @Autowired
    private CouponUseService couponUseService;



    @Override
    public void updateCouponInfoUseStatus(Long couponUseId, Long orderId) {
        CouponUse couponUse = new CouponUse();
        couponUse.setId(couponUseId);
        couponUse.setOrderId(orderId);
        couponUse.setCouponStatus("1");
        couponUse.setUsingTime(new Date());
        couponUseService.updateById(couponUse);
    }

    @Override
    public IPage<CouponUse> selectCouponUsePage(Page<CouponUse> pageParam, CouponUseQueryVo couponUseQueryVo) {
        //获取查询条件内容
        Long couponId = couponUseQueryVo.getCouponId();
        String couponStatus = couponUseQueryVo.getCouponStatus();
        String getTimeBegin = couponUseQueryVo.getGetTimeBegin();
        String getTimeEnd = couponUseQueryVo.getGetTimeEnd();

        //封装查询条件
        LambdaQueryWrapper<CouponUse> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(couponId)){
            lambdaQueryWrapper.eq(CouponUse::getCouponId, couponId);
        }
        if(!StringUtils.isEmpty(couponStatus)){
            lambdaQueryWrapper.eq(CouponUse::getCouponStatus, couponStatus);
        }
        if(!StringUtils.isEmpty(getTimeBegin)){
            lambdaQueryWrapper.ge(CouponUse::getGetTime, getTimeBegin);
        }
        if(!StringUtils.isEmpty(getTimeEnd)){
            lambdaQueryWrapper.le(CouponUse::getGetTime, getTimeEnd);
        }

        //条件查询+分页 优惠卷
        Page<CouponUse> page = couponUseService.page(pageParam, lambdaQueryWrapper);

        //把分页出来的记录 加入用户信息
        List<CouponUse> couponInfoList = page.getRecords();

        for (CouponUse c:couponInfoList) {
            //把扩展信息加入集合中的每一个对象
            c = this.getUserInfoById(c);
        }

        return page;
    }

    private CouponUse getUserInfoById(CouponUse c) {
        //获取用户id
        Long userId = c.getUserId();
        if (userId!=null){
            //远程调用
            UserInfo userInfo = feginUserInfoService.getById(userId);
            //获取用户名和手机号，封装成Map返回
            String userNickName = userInfo.getNickName();
            String userPhone = userInfo.getPhone();
            //这个c.getParam()是因为c继承了BaseEntity的实体类，是BaseEntity的扩展
            //这里getParam()代表了扩展的属性，而这里往里放的两个数据都是扩展属性
            c.getParam().put("nickName", userNickName);
            c.getParam().put("phone", userPhone);
        }
        return c;
    }
}
