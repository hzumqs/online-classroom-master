package com.cc.service_vod.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.model.vod.Chapter;
import com.entity.vo.vod.ChapterVo;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author cc
 * @since 2022-07-11
 */
public interface ChapterService extends IService<Chapter> {

    List<ChapterVo> getNestedTreeList(Long courseId);

    void removeChapterByCourseId(Long id);
}
