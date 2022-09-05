package com.cc.service_vod.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entity.model.vod.Teacher;
import com.entity.result.Result;
import com.cc.service_vod.service.TeacherService;
import com.entity.vo.vod.TeacherQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author cc
 * @since 2022-07-05
 */
@Api(tags = "讲师管理接口")
@RestController
@RequestMapping("/vod/teacher")
//@CrossOrigin
public class TeacherController {

    @Resource
    private TeacherService teacherService;

    //查询所有讲师列表
    @ApiOperation("所有讲师列表")
    @GetMapping("findAll")
    public Result<List<Teacher>> findAll(){
        List<Teacher> list = teacherService.list();
        return Result.success(list).message("信息返回成功");
    }

    @ApiOperation("逻辑删除讲师")
    @DeleteMapping("remove/{id}")
    public Result<Boolean> delete(@PathVariable("id") Long id) {
        return Result.success(teacherService.removeById(id));
    }

    @ApiOperation("按条件分页查询")
    @PostMapping("/findQueryPage/{page}/{limit}")
    public Result findQueryPage(@ApiParam(name = "page", value = "当前页码", required = true)
                                    @PathVariable Long page,
                                @ApiParam(name = "limit", value = "每页记录数", required = true)
                                    @PathVariable Long limit,
                                @ApiParam(name = "teacherVo", value = "查询对象", required = false)
                                    @RequestBody(required = false) TeacherQueryVo teacherQueryVo) {
        //创建page对象，确定 当前页和每页多少条数据
        //分页构造器
        Page pageInfo = new Page(page, limit);
        LambdaQueryWrapper<Teacher> lambdaQueryWrapper = new LambdaQueryWrapper();
        //对TeacherQueryVo，也就是查询条件对象做出判断，如果为空则没有查询条件
        if (teacherQueryVo == null) {
            //无查询条件直接就遍历了
            Page teacherQueryPage=teacherService.page(pageInfo,null);
            return Result.success(teacherQueryPage);
        }else {
            //根据条件进行查询
            String name = teacherQueryVo.getName();
            Integer level = teacherQueryVo.getLevel();
            String joinDateBegin= teacherQueryVo.getJoinDateBegin();
            String joinDateEnd= teacherQueryVo.getJoinDateEnd();
            //封装查询条件
            if (!StringUtils.isEmpty(name)) {
                lambdaQueryWrapper.like(Teacher::getName, name);
            }
            if (!StringUtils.isEmpty(level)) {
                lambdaQueryWrapper.eq(Teacher::getLevel, level);
            }
            if (!StringUtils.isEmpty(joinDateBegin)) {//加入日期大于这个日期区间的
                lambdaQueryWrapper.ge(Teacher::getJoinDate, joinDateBegin);
            }
            if (!StringUtils.isEmpty(joinDateEnd)) {//退出日期小于这个日期区间的
                lambdaQueryWrapper.le(Teacher::getJoinDate, joinDateEnd);
            }
        }

        IPage teacherQueryPage = teacherService.page(pageInfo, lambdaQueryWrapper);
        return Result.success(teacherQueryPage);
    }

    //添加讲师
    @ApiOperation("添加讲师")
    @PostMapping("/save")
    public Result saveTeacher(@RequestBody Teacher teacher) {
        teacherService.save(teacher);
        return Result.success("添加成功");
    }

    //根据讲师id获取讲师信息
    @ApiOperation("根据id获取讲师信息")
    @GetMapping("getTeacher/{id}")
    public Result getTeacherById(@PathVariable Long id){
        Teacher teacher=teacherService.getById(id);
        return Result.success(teacher);
    }

    //更新教师信息
    @ApiOperation(value = "修改")
    @PutMapping("updateTeacher")
    public Result updateById(@RequestBody Teacher teacher) {
        teacherService.updateById(teacher);
        return Result.success("更新成功");
    }

    //批量删除讲师
    @ApiOperation(value = "根据id列表删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        teacherService.removeByIds(idList);
        return Result.success("删除成功");
    }

    /**
     * 查询直播课程教师
     * @param id
     * @return
     */
    @ApiOperation("根据id查询")
    @GetMapping("inner/getTeacher/{id}")
    public Teacher getTeacherLive(@PathVariable Long id) {
        Teacher teacher = teacherService.getById(id);
        return teacher;
    }
}

