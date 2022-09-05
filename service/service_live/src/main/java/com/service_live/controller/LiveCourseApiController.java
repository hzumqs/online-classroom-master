package com.service_live.controller;

import com.alibaba.fastjson.JSONObject;
import com.entity.result.Result;
import com.entity.utils.AuthContextHolder;
import com.service_live.service.LiveCourseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("api/live/liveCourse")
public class LiveCourseApiController {

    @Resource
    private LiveCourseService liveCourseService;

    @ApiOperation(value = "获取用户access_token")
    @GetMapping("getPlayAuth/{id}")
    public Result<JSONObject> getPlayAuth(@PathVariable Long id) {
        JSONObject object = liveCourseService.getPlayAuth(id, AuthContextHolder.getUserId());
        return Result.success(object);
    }

    @ApiOperation("根据ID查询课程")
    @GetMapping("getInfo/{courseId}")
    public Result getInfo(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable Long courseId){
        Map<String, Object> map = liveCourseService.getInfoById(courseId);
        return Result.success(map);
    }

}