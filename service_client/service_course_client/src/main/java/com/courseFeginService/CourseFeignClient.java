package com.courseFeginService;


import com.entity.model.vod.Course;
import com.entity.model.vod.Teacher;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

//@FeignClient(value = "接口所在的服务名")
@FeignClient(value = "service-vod")
public interface CourseFeignClient {

    @ApiOperation("根据ID查询课程信息")
    @GetMapping("/api/vod/course/inner/getById/{courseId}")
    public Course getById(@PathVariable Long courseId);

    @ApiOperation("根据关键字查询课程")
    @GetMapping("/api/vod/course/inner/findByKeyword/{keyword}")
    List<Course> findByKeyword(@PathVariable String keyword);

    @ApiOperation("查询直播课程教师")
    @GetMapping("/vod/teacher/inner/getTeacher/{id}")
    Teacher getTeacherLive(@PathVariable Long id);
}
