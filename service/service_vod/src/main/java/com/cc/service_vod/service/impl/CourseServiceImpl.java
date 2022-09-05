package com.cc.service_vod.service.impl;

import com.cc.service_vod.service.*;
import com.entity.model.vod.Course;
import com.entity.model.vod.CourseDescription;
import com.entity.model.vod.Subject;
import com.entity.model.vod.Teacher;
import com.entity.vo.vod.*;
import com.cc.service_vod.mapper.CourseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-04-22
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CourseDescriptionService courseDescriptionService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private CourseDescriptionService descriptionService;

    //根据课程分类查询课程列表（分页）
    @Override
    public Map<String,Object> findPage(Page<Course> pageParam,
                                       CourseQueryVo courseQueryVo) {
        //获取条件值
        String title = courseQueryVo.getTitle();//课程名称
        Long subjectId = courseQueryVo.getSubjectId();//二级分类
        Long subjectParentId = courseQueryVo.getSubjectParentId();//一级分类
        Long teacherId = courseQueryVo.getTeacherId();//讲师

        //判断条件值是否为空，封装
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(title)) {
            wrapper.like("title",title);
        }
        if(!StringUtils.isEmpty(subjectId)) {
            wrapper.eq("subject_id",subjectId);
        }
        if(!StringUtils.isEmpty(subjectParentId)) {
            wrapper.eq("subject_parent_id",subjectParentId);
        }
        if(!StringUtils.isEmpty(teacherId)) {
            wrapper.eq("teacher_id",teacherId);
        }
        //调用方法进行条件分页查询
        Page<Course> pages = baseMapper.selectPage(pageParam, wrapper);

        //获取分页数据
        long totalCount = pages.getTotal();//总记录数
        long totalPage = pages.getPages();//总页数
        long currentPage = pages.getCurrent();//当前页
        long size = pages.getSize();//每页记录数
        //每页数据集合
        List<Course> records = pages.getRecords();

        //封装其他数据（获取讲师名称 和 课程分类名称）
        records.stream().forEach(item -> {
            this.getTeacherAndSubjectName(item);
        });

        //map集合返回
        Map<String,Object> map = new HashMap<>();
        map.put("totalCount",totalCount);
        map.put("totalPage",totalPage);
        map.put("records",records);
        return map;
    }

    /**
     * 添加课程
     * @param courseFormVo
     * @return
     */
    @Override
    public Long saveCourse(CourseFormVo courseFormVo) {
        //课程对象，course
        Course course = new Course();
        BeanUtils.copyProperties(courseFormVo,course);
        baseMapper.insert(course);

        //课程描述对象，courseDescription
        CourseDescription courseDescription = new CourseDescription();
        Long courseId = course.getId();
        courseDescription.setCourseId(courseId);
        courseDescription.setDescription(courseFormVo.getDescription());
        courseDescriptionService.save(courseDescription);
        return courseId;
    }

    /**
     * 根据ID获取课程Vo（课程基本信息+课程描述）
     * @param id
     * @return
     */
    @Override
    public CourseFormVo getCourseFromVo(Long id) {
        //从course表中取数据
        Course course = baseMapper.selectById(id);
        if(course == null){
            return null;
        }
        //从course_description表中取数据
        CourseDescription courseDescription = courseDescriptionService.getById(id);
        //创建courseInfoForm对象
        CourseFormVo courseFormVo = new CourseFormVo();
        BeanUtils.copyProperties(course, courseFormVo);
        if(courseDescription != null){
            courseFormVo.setDescription(courseDescription.getDescription());
        }
        return courseFormVo;
    }

    /**
     * 更新课程
     * @param courseFormVo
     */
    @Override
    public void updateCourseById(CourseFormVo courseFormVo) {
        //课程信息
        Course course = new Course();
        //拆分Form对象
        BeanUtils.copyProperties(courseFormVo, course);
        //update数据库
        baseMapper.updateById(course);

        //课程描述
        CourseDescription courseDescription = new CourseDescription();
        //拆分Form对象
        BeanUtils.copyProperties(courseFormVo, courseDescription);
        //为课程描述设置ID
        courseDescription.setId(course.getId());
        //update数据库
        courseDescriptionService.updateById(courseDescription);

    }


    /**
     * 根据id查询课程发布信息
     * @param id
     * @return
     */
    @Override
    public CoursePublishVo getCoursePublishVo(Long id) {
        return baseMapper.selectCoursePublishVoById(id);
    }

    /**
     * 更新课程发布状态
     * @param id
     * @return
     */
    @Override
    public int publishCourseById(Long id) {
        Course course = baseMapper.selectById(id);
        course.setStatus(1);
        course.setPublishTime(new Date());
        return baseMapper.updateById(course);
    }

    /**
     * 删除课程
     * @param id
     */
    @Override
    public void removeCourseById(Long id) {
        //根据课程id删除小节
        videoService.removeVideoByCourseId(id);
        //根据课程id删除章节
        chapterService.removeChapterByCourseId(id);
        //根据课程id删除描述
        courseDescriptionService.removeById(id);
        //根据课程id删除课程
        baseMapper.deleteById(id);
    }

    //根据id查询课程
    @Override
    public Map<String, Object> getInfoById(Long id) {
        //更新流量量
        Course course = baseMapper.selectById(id);
        course.setViewCount(course.getViewCount() + 1);
        baseMapper.updateById(course);

        Map<String, Object> map = new HashMap<>();
        CourseVo courseVo = baseMapper.selectCourseVoById(id);
        List<ChapterVo> chapterVoList = chapterService.getNestedTreeList(id);
        CourseDescription courseDescription = descriptionService.getById(id);
        Teacher teacher = teacherService.getById(course.getTeacherId());

        //TODO后续完善
        Boolean isBuy = false;

        map.put("courseVo", courseVo);
        map.put("chapterVoList", chapterVoList);
        map.put("description", null != courseDescription ?
                courseDescription.getDescription() : "");
        map.put("teacher", teacher);
        map.put("isBuy", isBuy);//是否购买
        return map;
    }

    private Course getTeacherAndSubjectName(Course item) {
        //获取讲师ID
        Long teacherId = item.getTeacherId();
        //查询讲师名称
        Teacher teacher=teacherService.getById(teacherId);
        if(teacher != null) {
            item.getParam().put("teacherName",teacher.getName());
        }
        //查询分类名称
        Subject subjectOne = subjectService.getById(item.getSubjectParentId());
        if(subjectOne != null) {
            item.getParam().put("subjectParentTitle",subjectOne.getTitle());
        }
        Subject subjectTwo = subjectService.getById(item.getSubjectId());
        if(subjectTwo != null) {
            item.getParam().put("subjectTitle",subjectTwo.getTitle());
        }
        return item;
    }

    @Override
    public List<Course> findlist() {
        List<Course> list = baseMapper.selectList(null);
        list.stream().forEach(item -> {
            this.getTeacherAndSubjectName(item);
        });
        return list;
    }
}
