package com.service_order.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.model.order.OrderInfo;
import com.entity.vo.order.OrderFormVo;
import com.entity.vo.order.OrderInfoQueryVo;
import com.entity.vo.order.OrderInfoVo;

import java.util.Map;

/**
 * <p>
 * 订单表 订单表 服务类
 * </p>
 *
 * @author cc
 * @since 2022-07-14
 */
public interface OrderInfoService extends IService<OrderInfo> {

    Map<String, Object> findPageOrderInfo(Page<OrderInfo> pageParam, OrderInfoQueryVo orderInfoQueryVo);

    Long submitOrder(OrderFormVo orderFormVo);

    OrderInfoVo getOrderInfoVoById(Long id);

    void updateOrderStatus(String out_trade_no);



}
