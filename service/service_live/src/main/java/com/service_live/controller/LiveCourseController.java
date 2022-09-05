package com.service_live.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entity.model.live.LiveCourse;
import com.entity.model.live.LiveCourseAccount;
import com.entity.result.Result;
import com.entity.vo.live.LiveCourseConfigVo;
import com.entity.vo.live.LiveCourseFormVo;
import com.service_live.service.LiveCourseAccountService;
import com.service_live.service.LiveCourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 直播课程表 前端控制器
 * </p>
 *
 * @author cc
 * @since 2022-07-20
 */
@Api(tags = "直播课程列表")
@RestController
@RequestMapping("/admin/live/liveCourse")
public class LiveCourseController {

    @Resource
    private LiveCourseService liveCourseService;

    @Autowired
    private LiveCourseAccountService liveCourseAccountService;


    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable("page") Long page,
                        @PathVariable("limit") Long limit){
        //封装分页对象
        Page pageParam = new Page(page, limit);
        IPage<LiveCourse> pageModel = liveCourseService.selectPage(pageParam);
        return Result.success(pageModel);
    }

    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody LiveCourseFormVo liveCourseVo) {
        liveCourseService.save(liveCourseVo);
        return Result.success(null);
    }

    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result<LiveCourse> get(@PathVariable Long id) {
        LiveCourse liveCourse = liveCourseService.getById(id);
        return Result.success(liveCourse);
    }

    @ApiOperation(value = "获取")
    @GetMapping("getInfo/{id}")
    public Result<LiveCourseFormVo> getInfo(@PathVariable Long id) {
        return Result.success(liveCourseService.getLiveCourseFormVo(id));
    }

    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody LiveCourseFormVo liveCourseVo) {
        liveCourseService.updateLiveById(liveCourseVo);
        return Result.success(null);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        liveCourseService.removeLive(id);
        return Result.success(null);
    }


    @ApiOperation(value = "获取")
    @GetMapping("getLiveCourseAccount/{id}")
    public Result<LiveCourseAccount> getLiveCourseAccount(@PathVariable Long id) {
        return Result.success(liveCourseAccountService.getByLiveCourseId(id));
    }

    @ApiOperation(value = "获取")
    @GetMapping("getCourseConfig/{id}")
    public Result getCourseConfig(@PathVariable Long id) {
        return Result.success(liveCourseService.getCourseConfig(id));
    }

    @ApiOperation(value = "修改配置")
    @PutMapping("updateConfig")
    public Result updateConfig(@RequestBody LiveCourseConfigVo liveCourseConfigVo) {
        liveCourseService.updateConfig(liveCourseConfigVo);
        return Result.success(null);
    }

    @ApiOperation(value = "获取最近的直播")
    @GetMapping("findLatelyList")
    public Result findLatelyList() {
        return Result.success(liveCourseService.findLatelyList());
    }

}

