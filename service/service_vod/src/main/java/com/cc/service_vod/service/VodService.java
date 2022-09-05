package com.cc.service_vod.service;

import java.io.InputStream;

public interface VodService{
    String uploadVideo(InputStream inputStream, String originalFilename);

    void removeVideo(String videoSourceId);

    Object getPlayAuth(Long courseId, Long videoId);
}
