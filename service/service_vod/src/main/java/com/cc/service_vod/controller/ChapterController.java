package com.cc.service_vod.controller;


import com.cc.service_vod.service.ChapterService;
import com.entity.model.vod.Chapter;
import com.entity.result.Result;
import com.entity.vo.vod.ChapterVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author cc
 * @since 2022-07-11
 */
@Api(tags = "章节管理接口")
@RestController
@RequestMapping("/admin/vod/chapter")
//@CrossOrigin
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    //获取章节小结列表
    @ApiOperation("嵌套章节数据列表")
    @GetMapping("getNestedTreeList/{courseId}")
    public Result getNestedTreeList(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable Long courseId){

        List<ChapterVo> chapterVoList = chapterService.getNestedTreeList(courseId);
        return Result.success(chapterVoList);
    }

    //2 添加章节
    @PostMapping("save")
    public Result save(@RequestBody Chapter chapter) {
        chapterService.save(chapter);
        return Result.success();
    }

    //3 修改-根据id查询
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        Chapter chapter = chapterService.getById(id);
        return Result.success(chapter);
    }

    //4 修改-最终实现
    @PostMapping("update")
    public Result update(@RequestBody Chapter chapter) {
        chapterService.updateById(chapter);
        return Result.success();
    }

    //5 删除章节
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        chapterService.removeById(id);
        return Result.success();
    }
}

