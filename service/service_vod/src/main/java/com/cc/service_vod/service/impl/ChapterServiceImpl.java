package com.cc.service_vod.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cc.service_vod.service.ChapterService;
import com.cc.service_vod.service.VideoService;
import com.entity.model.vod.Chapter;
import com.entity.model.vod.Video;
import com.cc.service_vod.mapper.ChapterMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entity.vo.vod.ChapterVo;
import com.entity.vo.vod.VideoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author cc
 * @since 2022-07-11
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    @Autowired
    private VideoService videoService;


    /**
     * 嵌套章节数据列表
     * @param courseId
     * @return
     */
    @Override
    public List<ChapterVo> getNestedTreeList(Long courseId) {
        //最终结果数据List集合
        List<ChapterVo> chapterVoList = new ArrayList<>();

        //根据课程ID获取章节
        LambdaQueryWrapper<Chapter> chapterLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chapterLambdaQueryWrapper.eq(Chapter::getCourseId, courseId);
        chapterLambdaQueryWrapper.orderByAsc(Chapter::getSort, Chapter::getId);
        List<Chapter> chapterList= baseMapper.selectList(chapterLambdaQueryWrapper);

        //根据章节来获取章节下视频
        LambdaQueryWrapper<Video> videoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        videoLambdaQueryWrapper.eq(Video::getCourseId, courseId);
        videoLambdaQueryWrapper.orderByAsc(Video::getSort, Video::getId);
        List<Video> videoList = videoService.list(videoLambdaQueryWrapper);

        //填充列表数据：Chapter列表
        for (int i = 0; i < chapterList.size(); i++) {
            Chapter chapter = chapterList.get(i);

            //创建ChapterVo对象
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(chapter, chapterVo);
            chapterVoList.add(chapterVo);

            //填充列表数据：Video列表
            List<VideoVo> videoVoList = new ArrayList<>();
            for (int j = 0; j < videoList.size(); j++) {
                Video video = videoList.get(j);
                if(chapter.getId().equals(video.getChapterId())){

                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(video, videoVo);
                    videoVoList.add(videoVo);
                }
            }
            chapterVo.setChildren(videoVoList);
        }
        return chapterVoList;
    }

    //根据课程id删除章节
    @Override
    public void removeChapterByCourseId(Long id) {
        QueryWrapper<Chapter> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id",id);
        baseMapper.delete(wrapper);
    }
}
