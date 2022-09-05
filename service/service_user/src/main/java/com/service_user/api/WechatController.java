package com.service_user.api;

import com.alibaba.fastjson.JSON;
import com.entity.model.user.UserInfo;
import com.entity.utils.JwtHelper;
import com.service_user.service.UserInfoService;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;

@Controller
@RequestMapping("/api/user/wechat")
public class WechatController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private WxMpService wxMpService;

    @Value("${wechat.userInfoUrl}")
    private String userInfoUrl;

    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl, HttpServletRequest request) {
        String redirectURL = wxMpService.oauth2buildAuthorizationUrl(userInfoUrl,
                WxConsts.OAUTH2_SCOPE_USER_INFO,
                URLEncoder.encode(returnUrl.replace("guiguketan", "#")));
        return "redirect:" + redirectURL;
    }

    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) throws Exception {
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = this.wxMpService.oauth2getAccessToken(code);
        String openId = wxMpOAuth2AccessToken.getOpenId();

        System.out.println("【微信网页授权】openId={}"+openId);

        WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
        System.out.println("【微信网页授权】wxMpUser={}"+ JSON.toJSONString(wxMpUser));

        //数据库查这个用户在不在
        UserInfo userInfo = userInfoService.getByOpenid(openId);
        if(null == userInfo) {
            //不在的话新插数据
            userInfo = new UserInfo();
            userInfo.setOpenId(openId);
            userInfo.setUnionId(wxMpUser.getUnionId());
            userInfo.setNickName(wxMpUser.getNickname());
            userInfo.setAvatar(wxMpUser.getHeadImgUrl());
            userInfo.setSex(wxMpUser.getSexId());
            userInfo.setProvince(wxMpUser.getProvince());

            userInfoService.save(userInfo);
        }
        //生成token
        String token = JwtHelper.createToken(userInfo.getId(), userInfo.getNickName());
        if(returnUrl.indexOf("?") == -1) {
            return "redirect:" + returnUrl + "?token=" + token;
        } else {
            return "redirect:" + returnUrl + "&token=" + token;
        }
    }
}
