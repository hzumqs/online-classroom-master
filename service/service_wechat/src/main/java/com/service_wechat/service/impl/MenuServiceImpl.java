package com.service_wechat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.entity.model.wechat.Menu;
import com.entity.vo.wechat.MenuVo;
import com.service_wechat.mapper.MenuMapper;
import com.service_wechat.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单明细 订单明细 服务实现类
 * </p>
 *
 * @author cc
 * @since 2022-07-16
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {


    @Autowired
    private WxMpService wxMpService;

    /**
     * 删除菜单
     */
    @SneakyThrows
    @Override
    public void removeMenu() {
        wxMpService.getMenuService().menuDelete();
    }


    /**
     * 说明：
     * 自定义菜单最多包括3个一级菜单，每个一级菜单最多包含5个二级菜单。
     * 一级菜单最多4个汉字，二级菜单最多8个汉字，多出来的部分将会以“...”代替。
     * 创建自定义菜单后，菜单的刷新策略是，在用户进入公众号会话页或公众号profile页时，
     * 如果发现上一次拉取菜单的请求在5分钟以前，就会拉取一下菜单，如果菜单有更新，就会刷新客户端的菜单。
     * 测试时可以尝试取消关注公众账号后再次关注，则可以看到创建后的效果。
     * 实际上就是把自己菜单的JSON格式转换成微信认识的格式
     */
    @SneakyThrows
    @Override
    public void syncMenu() {
        List<MenuVo> menuVoList = this.findMenuInfo();
        //菜单，创建一个JSON格式数组，这里为什么用了JSON数组，是因为这里直接对接公众号
        //不再有Controller的JSON转换解析，所以这里直接做成JSON
        JSONArray buttonList = new JSONArray();
        //一级菜单
        for(MenuVo oneMenuVo : menuVoList) {
            JSONObject one = new JSONObject();
            //name是一级菜单的固定名称
            one.put("name", oneMenuVo.getName());
            //二级菜单
            JSONArray subButton = new JSONArray();
            for(MenuVo twoMenuVo : oneMenuVo.getChildren()) {
                JSONObject view = new JSONObject();
                //type是二级菜单的固定名称
                view.put("type", twoMenuVo.getType());
                //根据type的值进行判断，是按钮还是关键词触发返回信息
                if(twoMenuVo.getType().equals("view")) {
                    view.put("name", twoMenuVo.getName());
                    view.put("url", "45nj779995.zicp.fun:42510/#"
                            +twoMenuVo.getUrl());
                } else {
                    view.put("name", twoMenuVo.getName());
                    view.put("key", twoMenuVo.getMeunKey());
                }
                subButton.add(view);
            }
            //封装二级菜单，固定叫sub_button
            one.put("sub_button", subButton);
            //封装一级菜单
            buttonList.add(one);
        }
        //菜单整体封装，一级+二级 一起叫button
        JSONObject button = new JSONObject();
        button.put("button", buttonList);
        this.wxMpService.getMenuService().menuCreate(button.toJSONString());
    }



    /**
     * 获取所有的一级菜单
     * @return
     */
    @Override
    public List<Menu> findMenuOneInfo() {
        LambdaQueryWrapper<Menu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Menu::getParentId, 0);
        List menuOneInfo=baseMapper.selectList(lambdaQueryWrapper);
        return menuOneInfo;
    }

    /**
     * 获取一级菜单+二级菜单
     * @return
     */
    @Override
    public List<MenuVo> findMenuInfo() {
        /*//MenuVo中有List<MenuVo> children 属性，就可以把二级菜单放进去
        //先查一级菜单
        List<Menu> menuOneInfo=this.findMenuOneInfo();
        List<MenuVo> allMenuInfo = new ArrayList<>();
        //遍历一级菜单，把相关子菜单查出来，同时放入全部菜单列表中
        for (Menu oneMeno : menuOneInfo) {
            //一级菜单拷贝
            MenuVo menuVo = new MenuVo();
            BeanUtils.copyProperties(oneMeno, menuVo);

            Long menoId= oneMeno.getId();
            //查当前一级菜单下的二级子菜单
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("parent_id", menoId);

            List<MenuVo> childMenuList = baseMapper.selectList(queryWrapper);
            //Lambda排一下序
            childMenuList.stream().sorted(Comparator.comparing(MenuVo::getSort));

            //把二级菜单数据搬运到MenuVo中
            menuVo.setChildren(childMenuList);

            allMenuInfo.add(menuVo);
        }

        return allMenuInfo;*/
        //获取全部菜单
        List<MenuVo> list = new ArrayList<>();
        List<Menu> menuList = baseMapper.selectList(null);
        List<Menu> oneMenuList = menuList.stream().filter(menu -> menu.getParentId().longValue() == 0).collect(Collectors.toList());
        for(Menu oneMenu : oneMenuList) {
            MenuVo oneMenuVo = new MenuVo();
            BeanUtils.copyProperties(oneMenu, oneMenuVo);

            List<Menu> twoMenuList = menuList.stream()
                    .filter(menu -> menu.getParentId().longValue() == oneMenu.getId())
                    .sorted(Comparator.comparing(Menu::getSort))
                    .collect(Collectors.toList());
            List<MenuVo> children = new ArrayList<>();
            for(Menu twoMenu : twoMenuList) {
                MenuVo twoMenuVo = new MenuVo();
                BeanUtils.copyProperties(twoMenu, twoMenuVo);
                children.add(twoMenuVo);
            }
            oneMenuVo.setChildren(children);
            list.add(oneMenuVo);
        }
        return list;
    }
}


