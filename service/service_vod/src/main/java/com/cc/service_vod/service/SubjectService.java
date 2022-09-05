package com.cc.service_vod.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.model.vod.Subject;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author cc
 * @since 2022-07-10
 */
public interface SubjectService extends IService<Subject> {

    List<Subject> selectList(Long id);

    /**
     * 导出
     * @param response
     */
    void exportData(HttpServletResponse response);

    void importDictData(MultipartFile file);
}
