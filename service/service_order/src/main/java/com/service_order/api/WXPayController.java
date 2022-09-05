package com.service_order.api;

import com.entity.result.Result;
import com.service_order.service.OrderInfoService;
import com.service_order.service.WXPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "微信支付接口")
@RestController
@RequestMapping("/api/order/wxPay")
public class WXPayController {

    @Autowired
    private WXPayService wxPayService;

    @Autowired
    private OrderInfoService orderInfoService;

    @ApiOperation(value = "下单 小程序支付")
    @GetMapping("/createJsapi/{orderNo}")
    public Result createJsapi(
            @ApiParam(name = "orderNo", value = "订单No", required = true)
            @PathVariable("orderNo") String orderNo) {
        return Result.success(wxPayService.createJsapi(orderNo));
    }

    @ApiOperation(value = "查询支付状态")
    @GetMapping("/queryPayStatus/{orderNo}")
    public Result queryPayStatus(
            @ApiParam(name = "orderNo", value = "订单No", required = true)
            @PathVariable("orderNo") String orderNo) {

        System.out.println("orderNo:"+orderNo);
        //调用查询接口
        Map<String, String> resultMap = wxPayService.queryPayStatus(orderNo);
        if (resultMap == null) {//出错
            return Result.error(null).message("支付出错");
        }
        if ("SUCCESS".equals(resultMap.get("trade_state"))) {//如果成功
            //更改订单状态，处理支付结果
            String out_trade_no = resultMap.get("out_trade_no");
            System.out.println("out_trade_no:"+out_trade_no);
            orderInfoService.updateOrderStatus(out_trade_no);
            return Result.success(null).message("支付成功");
        }
        return Result.success(null).message("支付中");
    }


}