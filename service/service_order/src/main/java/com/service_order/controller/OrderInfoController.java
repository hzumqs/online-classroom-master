package com.service_order.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entity.model.order.OrderInfo;
import com.entity.result.Result;
import com.entity.vo.order.OrderInfoQueryVo;
import com.service_order.service.OrderInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 订单表 订单表 前端控制器
 * </p>
 *
 * @author cc
 * @since 2022-07-14
 */
@Api(tags = "订单管理")
@RestController
@RequestMapping(value="/admin/order/orderInfo")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    @ApiOperation("订单信息分页")
    @GetMapping("/{page}/{limit}")
    public Result page(@ApiParam(value = "第几页" ,required = true) @PathVariable Long page,
                       @ApiParam(value = "每页条数" ,required = true) @PathVariable Long limit,
                       @ApiParam(name = "orderInfoVo", value = "查询对象", required = false) OrderInfoQueryVo orderInfoQueryVo){
        //提前把分页对象给封装好了，再传给Service
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        Map<String, Object> map = orderInfoService.findPageOrderInfo(pageParam, orderInfoQueryVo);
        return Result.success(map);
    }


}

