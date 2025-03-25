package org.example.lockproject.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.lockproject.dao.TicketDAO;
import org.example.lockproject.enums.TicketDBTableEnums;
import org.example.lockproject.enums.TicketType;
import org.example.lockproject.mq.enums.NginxQueueEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.example.lockproject.service.TicketBookingRedisService.*;

@Slf4j
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Resource
    private TicketBookingNginxDbService ticketBookingNginxDbService;

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
            //存TicketToken 過期後代表沒有付款 key = ticketToken_userID_area
            String token = GetTicketTokenKeyToken(expiredKey);
            String userId = GetTicketTokenKeyUserId(expiredKey);
            String area = GetTicketTokenKeyArea(expiredKey);

            NginxQueueEnums nginxQueueEnums = NginxQueueEnums.parse(area);
            if (nginxQueueEnums == null) {
                log.error("監聽過期Token 沒有該區域 : {} , token : {}", area, token);
                return;
            }
            switch (nginxQueueEnums) {
                case nginxQA:
                    ticketBookingNginxDbService.checkAndUpdateTicketType(TicketDBTableEnums.NGINX_QA,TicketType.TICKET_PAYING,TicketType.TICKET_NOT_PAY ,token);
                    break;
                case nginxQB:
                    ticketBookingNginxDbService.checkAndUpdateTicketType(TicketDBTableEnums.NGINX_QB,TicketType.TICKET_PAYING,TicketType.TICKET_NOT_PAY ,token);
                    break;
                default:
                    log.error("監聽過期Token 沒有該區域 : {} , token : {}", area, token);
                    return;
            }
            log.info("過期key處理完成：{}", expiredKey);
        } catch (Exception e) {
            log.error("處理redis 過期的key異常：{}", expiredKey, e);
        }
    }

}
