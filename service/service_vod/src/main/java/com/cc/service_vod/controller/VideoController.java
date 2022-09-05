package com.cc.service_vod.controller;


import com.cc.service_vod.service.VideoService;
import com.entity.exception.CustomerException;
import com.entity.model.vod.Video;
import com.entity.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author cc
 * @since 2022-07-11
 */
@Api(tags = "课程视频接口")
@RestController
@RequestMapping("/admin/vod/video")
//@CrossOrigin
public class VideoController {

    @Autowired
    private VideoService videoService;

    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        Video video = videoService.getById(id);
        return Result.success(video);
    }

    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody Video video) {
        try {
            videoService.save(video);
        } catch (Exception e) {
            throw new CustomerException(500,"请检查填写内容！");
        }
        return Result.success(null);
    }

    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody Video video) {
        videoService.updateById(video);
        return Result.success(null);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        videoService.removeById(id);
        return Result.success(null);
    }


}

