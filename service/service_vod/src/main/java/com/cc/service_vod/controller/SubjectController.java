package com.cc.service_vod.controller;


import com.cc.service_vod.service.SubjectService;
import com.entity.model.vod.Subject;
import com.entity.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "课程分类管理")
@RestController
@RequestMapping("/admin/vod/subject")
//@CrossOrigin
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    //查询下一层课程分类
    //根据parent_id
    @ApiOperation("查询下一层的课程分类")
    @GetMapping("getChildSubject/{id}")
    public Result getChildSubject(@PathVariable Long id) {
        List<Subject> subjectList = subjectService.selectList(id);
        return Result.success(subjectList);
    }

    //查询下一层课程分类
    //根据parent_id
    //因为是下载，所以不需要返回什么数据
    @ApiOperation("课程分类导出")
    @GetMapping("exportData")
    public void exportSubjectData(HttpServletResponse response) {
        subjectService.exportData(response);
    }

    @ApiOperation(value = "课程分类导入")
    @PostMapping("importData")
    public Result importData(MultipartFile file) {
        subjectService.importDictData(file);
        return Result.success();
    }
}
