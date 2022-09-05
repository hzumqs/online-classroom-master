package com.service_order.service.impl;

import com.activityFeginService.ActivityFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.courseFeginService.CourseFeignClient;
import com.entity.exception.CustomerException;
import com.entity.model.activity.CouponInfo;
import com.entity.model.order.OrderDetail;
import com.entity.model.order.OrderInfo;
import com.entity.model.user.UserInfo;
import com.entity.model.vod.Course;
import com.entity.result.ResultCodeEnum;
import com.entity.utils.AuthContextHolder;
import com.entity.utils.OrderNoUtils;
import com.entity.vo.order.OrderFormVo;
import com.entity.vo.order.OrderInfoQueryVo;
import com.entity.vo.order.OrderInfoVo;
import com.feginService.FeginUserInfoService;
import com.service_order.mapper.OrderInfoMapper;
import com.service_order.service.OrderDetailService;
import com.service_order.service.OrderInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.service_user.service.CouponUseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 订单表 服务实现类
 * </p>
 *
 * @author cc
 * @since 2022-07-14
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private OrderDetailService orderDetailService;

    @Resource
    private CouponUseService couponUseService;

    @Resource
    private CourseFeignClient courseFeignClient;

    @Resource
    private ActivityFeignClient couponInfoFeignClient;

    @Resource
    private FeginUserInfoService feginUserInfoService;


    /**
     * 根据ID更新订单状态
     * @param out_trade_no
     */
    @Override
    public void updateOrderStatus(String out_trade_no) {
        //根据out_trade_no查询订单
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getOutTradeNo,out_trade_no);
        OrderInfo orderInfo = baseMapper.selectOne(wrapper);
        //更新订单状态 1 已经支付
        orderInfo.setOrderStatus("1");
        baseMapper.updateById(orderInfo);
    }

    /**
     * 根据订单id获取订单相关信息
     * @param id
     * @return
     */
    @Override
    public OrderInfoVo getOrderInfoVoById(Long id) {
        OrderInfo orderInfo = this.getById(id);
        OrderDetail orderDetail = orderDetailService.getById(id);

        OrderInfoVo orderInfoVo = new OrderInfoVo();
        BeanUtils.copyProperties(orderInfo, orderInfoVo);
        orderInfoVo.setCourseId(orderDetail.getCourseId());
        orderInfoVo.setCourseName(orderDetail.getCourseName());
        return orderInfoVo;
    }

    //生成点播课程订单
    @Override
    public Long submitOrder(OrderFormVo orderFormVo) {
        Long userId = AuthContextHolder.getUserId();
        Long courseId = orderFormVo.getCourseId();
        Long couponId = orderFormVo.getCouponId();
        //查询当前用户是否已有当前课程的订单
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getCourseId, courseId);
        queryWrapper.eq(OrderDetail::getUserId, userId);
        OrderDetail orderDetailExist = orderDetailService.getOne(queryWrapper);
        if(orderDetailExist != null){
            return orderDetailExist.getId(); //如果订单已存在，则直接返回订单id
        }

        //查询课程信息
        Course course = courseFeignClient.getById(courseId);
        if (course == null) {
            throw new CustomerException(ResultCodeEnum.DATA_ERROR.getCode(),
                    ResultCodeEnum.DATA_ERROR.getMessage());
        }

        //查询用户信息
        UserInfo userInfo = feginUserInfoService.getById(userId);
        if (userInfo == null) {
            throw new CustomerException(ResultCodeEnum.DATA_ERROR.getCode(),
                    ResultCodeEnum.DATA_ERROR.getMessage());
        }

        //优惠券金额
        BigDecimal couponReduce = new BigDecimal(0);
        if(null != couponId) {
            CouponInfo couponInfo = couponInfoFeignClient.getById(couponId);
            couponReduce = couponInfo.getAmount();
        }

        //创建订单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(userId);
        orderInfo.setNickName(userInfo.getNickName());
        orderInfo.setPhone(userInfo.getPhone());
        orderInfo.setProvince(userInfo.getProvince());
        orderInfo.setOriginAmount(course.getPrice());
        orderInfo.setCouponReduce(couponReduce);
        orderInfo.setFinalAmount(orderInfo.getOriginAmount().subtract(orderInfo.getCouponReduce()));
        orderInfo.setOutTradeNo(OrderNoUtils.getOrderNo());
        orderInfo.setTradeBody(course.getTitle());
        orderInfo.setOrderStatus("0");
        this.save(orderInfo);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderInfo.getId());
        orderDetail.setUserId(userId);
        orderDetail.setCourseId(courseId);
        orderDetail.setCourseName(course.getTitle());
        orderDetail.setCover(course.getCover());
        orderDetail.setOriginAmount(course.getPrice());
        orderDetail.setCouponReduce(new BigDecimal(0));
        orderDetail.setFinalAmount(orderDetail.getOriginAmount().subtract(orderDetail.getCouponReduce()));
        orderDetailService.save(orderDetail);

        //更新优惠券状态
        if(null != orderFormVo.getCouponUseId()) {
            couponInfoFeignClient.updateCouponInfoUseStatus(orderFormVo.getCouponUseId(), orderInfo.getId());
        }
        return orderInfo.getId();
    }


    @Override
    public Map<String, Object> findPageOrderInfo(Page<OrderInfo> pageParam, OrderInfoQueryVo orderInfoQueryVo) {
        //orderInfoQueryVo获取查询条件
        Long userId = orderInfoQueryVo.getUserId();
        String outTradeNo = orderInfoQueryVo.getOutTradeNo();
        String phone = orderInfoQueryVo.getPhone();
        String createTimeEnd = orderInfoQueryVo.getCreateTimeEnd();
        String createTimeBegin = orderInfoQueryVo.getCreateTimeBegin();
        Integer orderStatus = orderInfoQueryVo.getOrderStatus();

        //封装查询条件
        LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(orderStatus)) {
            lambdaQueryWrapper.eq(OrderInfo::getOrderStatus,orderStatus);
        }
        if(!StringUtils.isEmpty(userId)) {
            lambdaQueryWrapper.eq(OrderInfo::getUserId,userId);
        }
        if(!StringUtils.isEmpty(outTradeNo)) {
            lambdaQueryWrapper.eq(OrderInfo::getOutTradeNo,outTradeNo);
        }
        if(!StringUtils.isEmpty(phone)) {
            lambdaQueryWrapper.eq(OrderInfo::getPhone,phone);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            lambdaQueryWrapper.ge(OrderInfo::getCreateTime,createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            lambdaQueryWrapper.le(OrderInfo::getCreateTime,createTimeEnd);
        }
        //存储分页数据
        Page resultPage = baseMapper.selectPage(pageParam, lambdaQueryWrapper);
        //把分页中的数据都拿出来，准备后续的往里面塞订单信息
        long totalCount = resultPage.getTotal();
        long pageCount = resultPage.getPages();
        List<OrderInfo> record = resultPage.getRecords();

        //遍历，然后根据当前的订单id查询订单详情，这里主要是取课程的名称
        for (OrderInfo o:record) {
            o=this.getOrderDetail(o);
        }
        Map<String, Object> resultMap = new HashMap();
        resultMap.put("total",totalCount);
        resultMap.put("pageCount",pageCount);
        resultMap.put("records",record);
        return resultMap;
    }

    //查询订单详情数据，获取订单对应信息
    private OrderInfo getOrderDetail(OrderInfo orderInfo) {
        //订单id
        Long id = orderInfo.getId();
        //查询订单详情
        OrderDetail orderDetail = orderDetailService.getById(id);
        if(orderDetail != null) {
            String courseName = orderDetail.getCourseName();
            orderInfo.getParam().put("courseName",courseName);
        }
        return orderInfo;
    }
}
