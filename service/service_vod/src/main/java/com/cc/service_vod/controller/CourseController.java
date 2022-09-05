package com.cc.service_vod.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cc.service_vod.service.CourseService;
import com.entity.model.vod.Course;
import com.entity.result.Result;
import com.entity.vo.vod.CourseFormVo;
import com.entity.vo.vod.CoursePublishVo;
import com.entity.vo.vod.CourseQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author cc
 * @since 2022-07-11
 */
@Api(tags = "课程管理接口")
@RestController
@RequestMapping("/admin/vod/course")
//@CrossOrigin
public class CourseController {

    @Autowired
    private CourseService courseService;

    @ApiOperation("课程分页接口")
    @GetMapping("{page}/{limit}")
    public Result page(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,
            @ApiParam(name = "courseVo", value = "查询对象", required = false)
                    CourseQueryVo courseQueryVo) {
        Page<Course> pageParam = new Page<>(page, limit);
        //如果不确定用什么数据的话，就用Map接收，拿取对象都比较方便
        Map<String,Object> map = courseService.findPage(pageParam, courseQueryVo);
        return Result.success(map);
    }

    @ApiOperation("课程添加接口")
    @PostMapping("save")
    public Result addCourse(@ApiParam(name = "courseFormVo", value = "课程信息", required = true)
                                @RequestBody CourseFormVo courseFormVo) {
        Long courseId=courseService.saveCourse(courseFormVo);
        //把保存好的课程Id返回一下就好
        return Result.success(courseId);
    }

    @ApiOperation("获取课程表单接口")
    @GetMapping("get/{id}")
    public Result<CourseFormVo> addCourse(@ApiParam(name = "id", value = "课程id", required = true)
                            @PathVariable Long id) {
        CourseFormVo courseFormVo=courseService.getCourseFromVo(id);
        //把保存好的课程Id返回一下就好
        return Result.success(courseFormVo);
    }

    @ApiOperation("更新课程")
    @PostMapping("update")
    public Result updateById(@RequestBody CourseFormVo courseFormVo) {
        courseService.updateCourseById(courseFormVo);
        return Result.success(courseFormVo.getId());
    }

    @ApiOperation("根据id获取课程发布信息")
    @GetMapping("getCoursePublishVo/{id}")
    public Result getCoursePublishVoById(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable Long id){

        CoursePublishVo coursePublishVo = courseService.getCoursePublishVo(id);
        return Result.success(coursePublishVo);
    }

    @ApiOperation("更新课程发布状态")
    @PutMapping("publishCourseById/{id}")
    public Result publishCourseById(@ApiParam(value = "课程id",required = true)
                                    @PathVariable Long id
                                    ){
        int result=courseService.publishCourseById(id);
        return result==1 ? Result.success("课程发布成功") : Result.error("课程发布失败");
    }

    @ApiOperation("删除课程")
    @DeleteMapping("remove/{id}")
    public Result removeCourseById(@ApiParam(value = "课程id",required = true)
                                    @PathVariable Long id
                                        ){
        courseService.removeCourseById(id);
        return Result.success();
    }

    @GetMapping("findAll")
    public Result findAll() {
        List<Course> list = courseService.findlist();
        return Result.success(list);
    }

}














