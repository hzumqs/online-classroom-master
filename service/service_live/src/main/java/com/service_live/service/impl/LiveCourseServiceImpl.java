package com.service_live.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.courseFeginService.CourseFeignClient;
import com.entity.exception.CustomerException;
import com.entity.model.live.*;
import com.entity.model.user.UserInfo;
import com.entity.model.vod.Teacher;
import com.entity.utils.DateUtil;
import com.entity.vo.live.LiveCourseConfigVo;
import com.entity.vo.live.LiveCourseFormVo;
import com.entity.vo.live.LiveCourseGoodsView;
import com.entity.vo.live.LiveCourseVo;
import com.feginService.FeginUserInfoService;
import com.service_live.mapper.LiveCourseMapper;
import com.service_live.mtcloud.CommonResult;
import com.service_live.mtcloud.MTCloud;
import com.service_live.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 直播课程表 服务实现类
 * </p>
 *
 * @author cc
 * @since 2022-07-20
 */
@Service
public class LiveCourseServiceImpl extends ServiceImpl<LiveCourseMapper, LiveCourse> implements LiveCourseService {

    //注入远程调用
    @Autowired
    private CourseFeignClient teacherFeignClient;

    @Resource
    private LiveCourseAccountService liveCourseAccountService;

    @Resource
    private LiveCourseDescriptionService liveCourseDescriptionService;

    @Resource
    private MTCloud mtCloudClient;

    @Autowired
    private LiveCourseConfigService liveCourseConfigService;

    @Autowired
    private LiveCourseGoodsService liveCourseGoodsService;

    @Resource
    private FeginUserInfoService feginUserInfoService;

    @SneakyThrows
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Boolean save(LiveCourseFormVo liveCourseFormVo) {
        LiveCourse liveCourse = new LiveCourse();
        BeanUtils.copyProperties(liveCourseFormVo, liveCourse);

        Teacher teacher = teacherFeignClient.getTeacherLive(liveCourseFormVo.getTeacherId());
        HashMap<Object, Object> options = new HashMap<>();
        options.put("scenes", 2);//直播类型。1: 教育直播，2: 生活直播。默认 1，说明：根据平台开通的直播类型填写
        options.put("password", liveCourseFormVo.getPassword());
        String res = mtCloudClient.courseAdd(liveCourse.getCourseName(), teacher.getId().toString(), new DateTime(liveCourse.getStartTime()).toString("yyyy-MM-dd HH:mm:ss"), new DateTime(liveCourse.getEndTime()).toString("yyyy-MM-dd HH:mm:ss"), teacher.getName(), teacher.getIntro(), options);

        System.out.println("return:: "+res);
        CommonResult<JSONObject> commonResult = JSON.parseObject(res, CommonResult.class);
        if(Integer.parseInt(commonResult.getCode()) == MTCloud.CODE_SUCCESS) {
            JSONObject object = commonResult.getData();
            liveCourse.setCourseId(object.getLong("course_id"));
            baseMapper.insert(liveCourse);

            //保存课程详情信息
            LiveCourseDescription liveCourseDescription = new LiveCourseDescription();
            liveCourseDescription.setDescription(liveCourseFormVo.getDescription());
            liveCourseDescription.setLiveCourseId(liveCourse.getId());
            liveCourseDescriptionService.save(liveCourseDescription);

            //保存课程账号信息
            LiveCourseAccount liveCourseAccount = new LiveCourseAccount();
            liveCourseAccount.setLiveCourseId(liveCourse.getId());
            liveCourseAccount.setZhuboAccount(object.getString("bid"));
            liveCourseAccount.setZhuboPassword(liveCourseFormVo.getPassword());
            liveCourseAccount.setAdminKey(object.getString("admin_key"));
            liveCourseAccount.setUserKey(object.getString("user_key"));
            liveCourseAccount.setZhuboKey(object.getString("zhubo_key"));
            liveCourseAccountService.save(liveCourseAccount);
        } else {
            String getmsg = commonResult.getmsg();
            throw new CustomerException(20001,getmsg);
        }
        return true;
    }


    @Override
    public IPage<LiveCourse> selectPage(Page<LiveCourse> pageParam) {
        IPage page=baseMapper.selectPage(pageParam, null);
        List<LiveCourse> courseList = page.getRecords();
        for (LiveCourse liveCourse:courseList) {
            //远程调用，把教师相关信息放进去拓展属性中，因为继承了BaseMapper
            Teacher teacher = teacherFeignClient.getTeacherLive(liveCourse.getTeacherId());
            liveCourse.getParam().put("teacherName", teacher.getName());
            liveCourse.getParam().put("teacherLevel", teacher.getLevel());
        }
        return page;
    }

    //更新
    @Override
    public void updateLiveById(LiveCourseFormVo liveCourseFormVo) {
        //根据id获取直播课程基本信息
        LiveCourse liveCourse = baseMapper.selectById(liveCourseFormVo.getId());
        BeanUtils.copyProperties(liveCourseFormVo,liveCourse);
        //讲师
        Teacher teacher =
                teacherFeignClient.getTeacherLive(liveCourseFormVo.getTeacherId());

//             *   course_id 课程ID
//     *   account 发起直播课程的主播账号
//     *   course_name 课程名称
//     *   start_time 课程开始时间,格式:2015-01-01 12:00:00
//                *   end_time 课程结束时间,格式:2015-01-01 13:00:00
//                *   nickname 	主播的昵称
//                *   accountIntro 	主播的简介
//                *  options 		可选参数
        HashMap<Object, Object> options = new HashMap<>();
        try {
            String res = mtCloudClient.courseUpdate(liveCourse.getCourseId().toString(),
                    teacher.getId().toString(),
                    liveCourse.getCourseName(),
                    new DateTime(liveCourse.getStartTime()).toString("yyyy-MM-dd HH:mm:ss"),
                    new DateTime(liveCourse.getEndTime()).toString("yyyy-MM-dd HH:mm:ss"),
                    teacher.getName(),
                    teacher.getIntro(),
                    options);
            //返回结果转换，判断是否成功
            CommonResult<JSONObject> commonResult = JSON.parseObject(res, CommonResult.class);
            if(Integer.parseInt(commonResult.getCode()) == MTCloud.CODE_SUCCESS) {
                JSONObject object = commonResult.getData();
                //更新直播课程基本信息
                liveCourse.setCourseId(object.getLong("course_id"));
                baseMapper.updateById(liveCourse);
                //直播课程描述信息更新
                LiveCourseDescription liveCourseDescription =
                        liveCourseDescriptionService.getByLiveCourseId(liveCourse.getId());
                liveCourseDescription.setDescription(liveCourseFormVo.getDescription());
                liveCourseDescriptionService.updateById(liveCourseDescription);
            } else {
                throw new CustomerException(20001,"修改直播课程失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public LiveCourseFormVo getLiveCourseFormVo(Long id) {
        LiveCourse liveCourse = this.getById(id);
        LiveCourseDescription liveCourseDescription = liveCourseDescriptionService.getByLiveCourseId(id);

        LiveCourseFormVo liveCourseFormVo = new LiveCourseFormVo();
        BeanUtils.copyProperties(liveCourse, liveCourseFormVo);
        liveCourseFormVo.setDescription(liveCourseDescription.getDescription());
        return liveCourseFormVo;
    }

    //删除直播课程
    @Override
    public void removeLive(Long id) {
        //根据id查询直播课程信息
        LiveCourse liveCourse = baseMapper.selectById(id);
        if(liveCourse != null) {
            //获取直播courseid
            Long courseId = liveCourse.getCourseId();
            try {
                //调用方法删除平台直播课程
                mtCloudClient.courseDelete(courseId.toString());
                //删除表数据
                baseMapper.deleteById(id);
            } catch (Exception e) {
                e.printStackTrace();
                throw new CustomerException(20001,"删除直播课程失败");
            }
        }
    }

    @Override
    public LiveCourseConfigVo getCourseConfig(Long id) {
        LiveCourseConfigVo liveCourseConfigVo = new LiveCourseConfigVo();
        LiveCourseConfig liveCourseConfig = liveCourseConfigService.getByLiveCourseId(id);
        if(null != liveCourseConfig) {
            List<LiveCourseGoods> liveCourseGoodsList = liveCourseGoodsService.findByLiveCourseId(id);
            BeanUtils.copyProperties(liveCourseConfig, liveCourseConfigVo);
            liveCourseConfigVo.setLiveCourseGoodsList(liveCourseGoodsList);
        }
        return liveCourseConfigVo;
    }

    @Override
    public void updateConfig(LiveCourseConfigVo liveCourseConfigVo) {
        LiveCourseConfig liveCourseConfigUpt = new LiveCourseConfig();
        BeanUtils.copyProperties(liveCourseConfigVo, liveCourseConfigUpt);
        if(null == liveCourseConfigVo.getId()) {
            liveCourseConfigService.save(liveCourseConfigUpt);
        } else {
            liveCourseConfigService.updateById(liveCourseConfigUpt);
        }
        liveCourseGoodsService.remove(new LambdaQueryWrapper<LiveCourseGoods>().eq(LiveCourseGoods::getLiveCourseId, liveCourseConfigVo.getLiveCourseId()));
        if(!CollectionUtils.isEmpty(liveCourseConfigVo.getLiveCourseGoodsList())) {
            liveCourseGoodsService.saveBatch(liveCourseConfigVo.getLiveCourseGoodsList());
        }
        this.updateLifeConfig(liveCourseConfigVo);
    }
    /**
     * 上传直播配置
     * @param liveCourseConfigVo
     */
    @SneakyThrows
    private void updateLifeConfig(LiveCourseConfigVo liveCourseConfigVo) {
        LiveCourse liveCourse = this.getById(liveCourseConfigVo.getLiveCourseId());

        //参数设置
        HashMap<Object,Object> options = new HashMap<Object, Object>();
        //界面模式
        options.put("pageViewMode", liveCourseConfigVo.getPageViewMode());
        //观看人数开关
        JSONObject number = new JSONObject();
        number.put("enable", liveCourseConfigVo.getNumberEnable());
        options.put("number", number.toJSONString());
        //观看人数开关
        JSONObject store = new JSONObject();
        number.put("enable", liveCourseConfigVo.getStoreEnable());
        number.put("type", liveCourseConfigVo.getStoreType());
        options.put("store", number.toJSONString());
        //商城列表
        List<LiveCourseGoods> liveCourseGoodsList = liveCourseConfigVo.getLiveCourseGoodsList();
        if(!CollectionUtils.isEmpty(liveCourseGoodsList)) {
            List<LiveCourseGoodsView> liveCourseGoodsViewList = new ArrayList<>();
            for(LiveCourseGoods liveCourseGoods : liveCourseGoodsList) {
                LiveCourseGoodsView liveCourseGoodsView = new LiveCourseGoodsView();
                BeanUtils.copyProperties(liveCourseGoods, liveCourseGoodsView);
                liveCourseGoodsViewList.add(liveCourseGoodsView);
            }
            JSONObject goodsListEdit = new JSONObject();
            goodsListEdit.put("status", "0");
            options.put("goodsListEdit ", goodsListEdit.toJSONString());
            options.put("goodsList", JSON.toJSONString(liveCourseGoodsViewList));
        }

        String res = mtCloudClient.courseUpdateLifeConfig(liveCourse.getCourseId().toString(), options);

        CommonResult<JSONObject> commonResult = JSON.parseObject(res, CommonResult.class);
        if(Integer.parseInt(commonResult.getCode()) != MTCloud.CODE_SUCCESS) {
            throw new CustomerException(20001,"修改配置信息失败");
        }
    }
    @Override
    public List<LiveCourseVo> findLatelyList() {
        List<LiveCourseVo> liveCourseVoList = baseMapper.findLatelyList();

        for(LiveCourseVo liveCourseVo : liveCourseVoList) {
            liveCourseVo.setStartTimeString(new DateTime(liveCourseVo.getStartTime()).toString("yyyy年MM月dd HH:mm"));
            liveCourseVo.setEndTimeString(new DateTime(liveCourseVo.getEndTime()).toString("HH:mm"));

            Long teacherId = liveCourseVo.getTeacherId();
            Teacher teacher = teacherFeignClient.getTeacherLive(teacherId);
            liveCourseVo.setTeacher(teacher);

            liveCourseVo.setLiveStatus(this.getLiveStatus(liveCourseVo));
        }
        return liveCourseVoList;
    }

    /**
     * 直播状态 0：未开始 1：直播中 2：直播结束
     * @param liveCourse
     * @return
     */
    private int getLiveStatus(LiveCourse liveCourse) {
        // 直播状态 0：未开始 1：直播中 2：直播结束
        int liveStatus = 0;
        Date curTime = new Date();
        if(DateUtil.dateCompare(curTime, liveCourse.getStartTime())) {
            liveStatus = 0;
        } else if(DateUtil.dateCompare(curTime, liveCourse.getEndTime())) {
            liveStatus = 1;
        } else {
            liveStatus = 2;
        }
        return liveStatus;
    }

    @SneakyThrows
    @Override
    public JSONObject getPlayAuth(Long id, Long userId) {
        LiveCourse liveCourse = this.getById(id);
        UserInfo userInfo = feginUserInfoService.getById(userId);
        HashMap<Object,Object> options = new HashMap<Object, Object>();
        String res = mtCloudClient.courseAccess(liveCourse.getCourseId().toString(), userId.toString(), userInfo.getNickName(), MTCloud.ROLE_USER, 80*80*80, options);
        CommonResult<JSONObject> commonResult = JSON.parseObject(res, CommonResult.class);
        if(Integer.parseInt(commonResult.getCode()) == MTCloud.CODE_SUCCESS) {
            JSONObject object = commonResult.getData();
            System.out.println("access::"+object.getString("access_token"));
            return object;
        } else {
            throw new CustomerException(20001,"获取失败");
        }
    }

    @Override
    public Map<String, Object> getInfoById(Long id) {
        LiveCourse liveCourse = this.getById(id);
        liveCourse.getParam().put("startTimeString", new DateTime(liveCourse.getStartTime()).toString("yyyy年MM月dd HH:mm"));
        liveCourse.getParam().put("endTimeString", new DateTime(liveCourse.getEndTime()).toString("yyyy年MM月dd HH:mm"));
        Teacher teacher = teacherFeignClient.getTeacherLive(liveCourse.getTeacherId());
        LiveCourseDescription liveCourseDescription = liveCourseDescriptionService.getByLiveCourseId(id);

        Map<String, Object> map = new HashMap<>();
        map.put("liveCourse", liveCourse);
        map.put("liveStatus", this.getLiveStatus(liveCourse));
        map.put("teacher", teacher);
        if(null != liveCourseDescription) {
            map.put("description", liveCourseDescription.getDescription());
        } else {
            map.put("description", "");
        }
        return map;
    }
}
