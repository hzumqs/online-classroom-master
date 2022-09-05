package com.cc.service_vod.service.impl;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entity.model.vod.Subject;
import com.entity.model.vod.SubjectEeVo;
import com.cc.service_vod.mapper.SubjectMapper;
import com.cc.service_vod.service.SubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.service_vod.utils.SubjectListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author cc
 * @since 2022-07-10
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Autowired
    private SubjectListener subjectListener;

    @Override
    public List<Subject> selectList(Long id) {
        LambdaQueryWrapper<Subject> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Subject::getParentId, id);
        //baseMapper是ServiceImpl注入好的，直接调用就行
        List<Subject> subjectList = baseMapper.selectList(lambdaQueryWrapper);
        //遍历每个课程，看看他下面是否有子课程
        for (Subject s :subjectList) {
            //得到Subject的ID
            Long subjectId = s.getId();
            Boolean hasChildSubject = this.isChild(subjectId);
            s.setHasChildren(hasChildSubject);

        }
        return subjectList;
    }

    /**
     * 导出
     *
     * @param response
     */
    @Override
    public void exportData(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("课程分类", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

            //全查+搬运到Vo中
            List<Subject> dictList = baseMapper.selectList(null);
            List<SubjectEeVo> subjectEeVoList = new ArrayList<>();
            for (Subject s : dictList) {
                SubjectEeVo subjectEeVo = new SubjectEeVo();
                BeanUtils.copyProperties(s,subjectEeVo);
                subjectEeVoList.add(subjectEeVo);
            }

            //写入Excel
            EasyExcel.write(response.getOutputStream(), SubjectEeVo.class).sheet("课程信息").doWrite(subjectEeVoList);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 从Excel中往数据库导入课程资料
     * @param file file就是Excel文件，具体的格式校验在前端
     */
    @Override
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),
                    SubjectEeVo.class,subjectListener).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Boolean isChild(Long subjectId) {
        LambdaQueryWrapper<Subject> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Subject::getParentId, subjectId);
        Integer result=baseMapper.selectCount(lambdaQueryWrapper);
        if (result>0){
            //不为0则这个课程有父ID，那么这个传入的课程就有子课程
            return true;
        }
        //否则这个课程就没有子课程
        return false;
    }
}
