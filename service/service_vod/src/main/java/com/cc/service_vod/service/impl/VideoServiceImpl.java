package com.cc.service_vod.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cc.service_vod.service.VideoService;
import com.entity.model.vod.Video;
import com.cc.service_vod.mapper.VideoMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.service_vod.service.VodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author cc
 * @since 2022-07-11
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Autowired
    private VodService vodService;

    //根据课程id 删除小节及下属视频
    @Override
    public void removeVideoByCourseId(Long id) {
        //根据课程id 查询所有小节集合
        LambdaQueryWrapper<Video> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Video::getCourseId, id);
        List<Video> videoList = baseMapper.selectList(lambdaQueryWrapper);
        //遍历小节集合得到小节集合下的每个视频id
        for (Video v : videoList) {
            String videoId = v.getVideoSourceId();
            //如果腾讯云有存储的对应id，就得删掉
            if (!StringUtils.isEmpty(videoId)) {
                vodService.removeVideo(videoId);
            }
        }
        //章节都删完了，那么这个视频课程下属对应的就可以删了
        //提示一下，如果这个放在最前面就删掉的话，就无法得到id了，因此，腾讯云的内容应该先删
        baseMapper.delete(lambdaQueryWrapper);
    }

    //根据小节id删除小节删除视频
    @Override
    public void removeVideoById(Long id) {
        //根据id获取小节视频
        Video video=baseMapper.selectById(id);
        //获取video里存储在腾讯云里的id
        String videoId = video.getVideoSourceId();
        //不为空的话代表腾讯云里有视频，就得调用腾讯云的方法删掉
        if (!StringUtils.isEmpty(video)) {
            vodService.removeVideo(videoId);
        }
        //根据id删小节
        baseMapper.deleteById(id);
    }
}
