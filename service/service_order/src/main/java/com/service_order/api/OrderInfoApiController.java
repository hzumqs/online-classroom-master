package com.service_order.api;

import com.entity.result.Result;
import com.entity.vo.order.OrderFormVo;
import com.entity.vo.order.OrderInfoVo;
import com.service_order.service.OrderInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/order/orderInfo")
public class OrderInfoApiController {

    @Autowired
    private OrderInfoService orderInfoService;

    @ApiOperation("新增点播课程订单")
    @PostMapping("submitOrder")
    public Result submitOrder(@RequestBody OrderFormVo orderFormVo, HttpServletRequest request) {
        //返回订单id
        Long orderId = orderInfoService.submitOrder(orderFormVo);
        return Result.success(orderId);
    }

    @ApiOperation(value = "根据id获取订单信息")
    @GetMapping("getInfo/{id}")
    public Result getInfo(@PathVariable Long id) {
        OrderInfoVo orderInfoVo = orderInfoService.getOrderInfoVoById(id);
        return Result.success(orderInfoVo);
    }
}