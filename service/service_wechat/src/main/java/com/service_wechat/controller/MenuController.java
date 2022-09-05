package com.service_wechat.controller;


import com.alibaba.fastjson.JSONObject;
import com.entity.model.wechat.Menu;
import com.entity.result.Result;
import com.entity.vo.wechat.MenuVo;
import com.service_wechat.service.MenuService;
import com.service_wechat.utils.ConstantPropertiesUtil;
import com.service_wechat.utils.HttpClientUtils;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 订单明细 订单明细 前端控制器
 * </p>
 *
 * @author cc
 * @since 2022-07-16
 */
@RestController
@RequestMapping("/admin/wechat/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @ApiOperation(value = "删除菜单")
    @DeleteMapping("removeMenu")
    public Result removeMenu() {
        menuService.removeMenu();
        return Result.success(null);
    }

    //同步菜单方法
    @ApiOperation(value = "同步菜单")
    @GetMapping("syncMenu")
    public Result createMenu() throws WxErrorException {
        menuService.syncMenu();
        return Result.success(null);
    }


    //获取access_token
    @GetMapping("getAccessToken")
    public Result getAccessToken() {
        try {
            //拼接请求地址
            StringBuffer buffer = new StringBuffer();
            buffer.append("https://api.weixin.qq.com/cgi-bin/token");
            buffer.append("?grant_type=client_credential");
            // %s 代表占位符
            buffer.append("&appid=%s");
            buffer.append("&secret=%s");
            //请求地址设置参数，向占位符内传参
            String url = String.format(buffer.toString(),
                    ConstantPropertiesUtil.ACCESS_KEY_ID,
                    ConstantPropertiesUtil.ACCESS_KEY_SECRET);
            //发送http请求
            String tokenString = HttpClientUtils.get(url);
            //获取access_token
            JSONObject jsonObject = JSONObject.parseObject(tokenString);
            String access_token = jsonObject.getString("access_token");
            //返回
            return Result.success(access_token);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(null);
        }
    }

    //获取所有菜单，按照一级和二级菜单封装
    @GetMapping("findMenuInfo")
    public Result findMenuInfo() {
        List<MenuVo> list = menuService.findMenuInfo();
        return Result.success(list);
    }

    //获取所有一级菜单
    @GetMapping("findOneMenuInfo")
    public Result findOneMenuInfo() {
        List<Menu> list = menuService.findMenuOneInfo();
        return Result.success(list);
    }

    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        Menu menu = menuService.getById(id);
        return Result.success(menu);
    }

    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody Menu menu) {
        menuService.save(menu);
        return Result.success(null);
    }

    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody Menu menu) {
        menuService.updateById(menu);
        return Result.success(null);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        menuService.removeById(id);
        return Result.success(null);
    }

    @ApiOperation(value = "根据id列表删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        menuService.removeByIds(idList);
        return Result.success(null);
    }
}
