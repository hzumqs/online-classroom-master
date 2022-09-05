package com.cc.service_vod.controller;


import com.cc.service_vod.service.VideoService;
import com.cc.service_vod.utils.Signature;
import com.entity.result.Result;
import com.cc.service_vod.service.VodService;
import com.cc.service_vod.utils.ConstantPropertiesUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

@Api(tags = "腾讯云点播")
@RestController
@RequestMapping("/admin/vod")
//@CrossOrigin
public class VodController {

    @Autowired
    private VodService vodService;

    @Autowired
    private VideoService videoService;

    @PostMapping("upload")
    public Result uploadVideo( @ApiParam(name = "file", value = "文件", required = true)
                                   @RequestParam("file") MultipartFile file) throws IOException {
        //把传过来的文件，通过IO写入内存
        InputStream inputStream = file.getInputStream();
        //拿到初始文件名
        String originalFilename = file.getOriginalFilename();
        System.out.println(originalFilename);
        //调用Service上传方法，里面有腾讯云的上传整合SDK
        String videoId = vodService.uploadVideo(inputStream, originalFilename);
        return Result.success();
    }

    //删除视频
    @DeleteMapping("remove/{videoSourceId}")
    public Result removeVideo( @PathVariable String videoSourceId) {
        vodService.removeVideo(videoSourceId);
        return Result.success();
    }

    @GetMapping("sign")
    public Result sign() {
        Signature sign = new Signature();
        // 设置 App 的云 API 密钥
        sign.setSecretId(ConstantPropertiesUtil.ACCESS_KEY_ID);
        sign.setSecretKey(ConstantPropertiesUtil.ACCESS_KEY_SECRET);
        sign.setCurrentTime(System.currentTimeMillis() / 1000);
        sign.setRandom(new Random().nextInt(java.lang.Integer.MAX_VALUE));
        sign.setSignValidDuration(3600 * 24 * 2); // 签名有效期：2天
        try {
            String signature = sign.getUploadSignature();
            System.out.println("signature : " + signature);
            return Result.success(signature);
        } catch (Exception e) {
            System.out.print("获取签名失败");
            e.printStackTrace();
            return Result.success(null);
        }
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        videoService.removeVideoById(id);
        return Result.success();
    }
}
