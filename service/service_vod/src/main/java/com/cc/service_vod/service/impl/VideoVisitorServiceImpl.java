package com.cc.service_vod.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entity.model.vod.VideoVisitor;
import com.cc.service_vod.mapper.VideoVisitorMapper;
import com.cc.service_vod.service.VideoVisitorService;
import com.entity.vo.vod.VideoVisitorCountVo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VideoVisitorServiceImpl extends ServiceImpl<VideoVisitorMapper, VideoVisitor> implements VideoVisitorService {

    @Override
    public Map<String, Object> findCount(Long courseId, String startDate, String endDate) {
        //调用Mapper接口，把这个时间区间的集合起来，再做后续处理
        List<VideoVisitorCountVo> videoVisitorVoList =baseMapper.findCount(courseId, startDate, endDate);

        //创建map集合
        Map<String, Object> map = new HashMap<>();

        //因为要统计图表，一共两个集合，一个要放x轴的日期，一个要放y轴的数量。
        //把videoVisitorVoList中的数据剥离出来，放到对应集合中
        //这种Stream流的写法，是把每个videoVisitorVoList的VideoVisitorCountVo把joinTime属性拿出来，组成集合返回
        List dateList = videoVisitorVoList.stream().map(VideoVisitorCountVo::getJoinTime).collect(Collectors.toList());
        //这个和上面同理，只是要拿的对象不一样而已
        List countList = videoVisitorVoList.stream().map(VideoVisitorCountVo::getUserCount).collect(Collectors.toList());
        //放到map集合
        map.put("xData", dateList);
        map.put("yData", countList);
        return map;
    }

}
