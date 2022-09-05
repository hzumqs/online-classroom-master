package com.cc.service_vod.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.cc.service_vod.mapper.SubjectMapper;
import com.entity.model.vod.Subject;
import com.entity.model.vod.SubjectEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SubjectListener extends AnalysisEventListener<SubjectEeVo> {

    @Resource
    private SubjectMapper subjectMapper;

    //一行一行读取
    @Override
    public void invoke(SubjectEeVo subjectEeVo, AnalysisContext analysisContext) {
        //调用方法添加数据库
        Subject subject = new Subject();
        BeanUtils.copyProperties(subjectEeVo,subject);
        subjectMapper.insert(subject);
    }
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }
}