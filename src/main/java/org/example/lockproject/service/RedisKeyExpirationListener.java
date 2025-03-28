package org.example.lockproject.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.lockproject.client.service.ClientService;
import org.example.lockproject.enums.TicketDBTableEnums;
import org.example.lockproject.enums.TicketType;
import org.example.lockproject.mq.enums.NginxQueueEnums;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;


import static org.example.lockproject.service.TicketBookingRedisService.*;

@Slf4j
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Resource
    private TicketBookingNginxDbService ticketBookingNginxDbService;


    @Resource
    private ClientService clientService;

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
                    boolean updateA = ticketBookingNginxDbService.checkAndUpdateTicketType(TicketDBTableEnums.NGINX_QA, TicketType.TICKET_PAYING, TicketType.TICKET_NOT_PAY, token);
                    //確定狀態 TICKET_PAYING -> TICKET_NOT_PAY 後補票API
                    if(updateA){
                        clientService.addQuantityReq(NginxQueueEnums.nginxQA.name(),1);
                    }
                    break;
                case nginxQB:
                    boolean updateB = ticketBookingNginxDbService.checkAndUpdateTicketType(TicketDBTableEnums.NGINX_QB, TicketType.TICKET_PAYING, TicketType.TICKET_NOT_PAY, token);
                    //確定狀態 TICKET_PAYING -> TICKET_NOT_PAY 後補票API
                    if(updateB){
                        clientService.addQuantityReq(NginxQueueEnums.nginxQB.name(),1);
                    }
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


//    @Resource
//    private RedisMessageListenerContainer redisMessageListenerContainer;
//
//    /**
//     * 重新啟動 Redis 訂閱監聽器
//     */
//    private void restartRedisListener() {
//        try {
//            log.info("嘗試重啟 Redis 訂閱監聽器...");
//            redisMessageListenerContainer.stop();
//            redisMessageListenerContainer.start();
//            log.info("Redis 訂閱監聽器重新啟動成功");
//        } catch (Exception e) {
//            log.error("Redis 訂閱監聽器重啟失敗", e);
//        }
//    }

}
