package com.service_wechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.model.wechat.Menu;
import com.entity.vo.wechat.MenuVo;

import java.util.List;

/**
 * <p>
 * 订单明细 订单明细 服务类
 * </p>
 *
 * @author cc
 * @since 2022-07-16
 */
public interface MenuService extends IService<Menu> {

    List<Menu> findMenuOneInfo();

    List<MenuVo> findMenuInfo();

    void syncMenu();

    void removeMenu();
}
