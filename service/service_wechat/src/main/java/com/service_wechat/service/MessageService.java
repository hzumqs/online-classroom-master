package com.service_wechat.service;

import java.util.Map;

public interface MessageService {
    public String receiveMessage(Map<String, String> param);

    void pushPayMessage(long l);
}
