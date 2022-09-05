package com.service_order.service.impl;


import com.entity.model.order.OrderDetail;
import com.service_order.mapper.OrderDetailMapper;
import com.service_order.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单明细 订单明细 服务实现类
 * </p>
 *
 * @author cc
 * @since 2022-07-14
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}
