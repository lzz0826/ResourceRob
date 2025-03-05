package org.example.lockproject.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * 針對redis數據失效事件，進行數據處理
     *
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 用戶做自己的業務處理即可,注意message.toString()可以獲取失效的key
        String expiredKey = message.toString();
        log.info("onMessage --> redis 過期的key是：{}", expiredKey);
        try {
            // TODO 對過期key進行處理

            System.out.println("key:" + expiredKey);
            log.info("過期key處理完成：{}", expiredKey);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("處理redis 過期的key異常：{}", expiredKey, e);
        }
    }

}
