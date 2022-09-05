package com.cc.service_vod.api;

import com.cc.service_vod.service.VodService;
import com.entity.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "腾讯云视频播放")
@RestController
@RequestMapping("/api/vod")
public class VodApiController {

    @Autowired
    private VodService vodService;

    @GetMapping("getPlayAuth/{courseId}/{videoId}")
    public Result getPlayAuth(@ApiParam(value = "课程ID",required = true)
                          @PathVariable("courseId") Long courseId,
                              @ApiParam(value = "视频ID",required = true)
                          @PathVariable("videoId") Long videoId ){
        return  Result.success(vodService.getPlayAuth(courseId, videoId));
    }

}
