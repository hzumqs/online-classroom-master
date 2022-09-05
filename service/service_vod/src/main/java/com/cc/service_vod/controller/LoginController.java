package com.cc.service_vod.controller;

import com.entity.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/vod/user")
//@CrossOrigin
public class LoginController {
    /**
     * 登录
     * @return
     */
    @PostMapping("login")
    public Result login() {
        Map<String, Object> map = new HashMap<>();
        //{"code":20000,"message":"成功","data":{"token":"admin"}}
        map.put("token","admin");
        return Result.success(map);
    }
    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("info")
    public Result info() {
        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name","admin");
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        return Result.success(map);
    }
    /**
     * 退出
     * @return
     */
    @PostMapping("logout")
    public Result logout(){
        return Result.success();
    }
}