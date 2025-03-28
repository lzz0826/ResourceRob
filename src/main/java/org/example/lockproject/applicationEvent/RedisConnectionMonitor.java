//package org.example.lockproject.applicationEvent;
//
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.dao.DataAccessException;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class RedisConnectionMonitor {
//
//    @Resource
//    private RedisMessageListenerContainer redisMessageListenerContainer;
//
//    @Resource
//    private RedisConnectionFactory redisConnectionFactory;
//
//    /**
//     * 每 10 秒檢查一次 Redis 連線狀態
//     */
//    @Scheduled(fixedDelay = 10000)  // 上一次執行完後，等 10 秒再執行下一次
//    public void checkRedisConnection() {
//        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
//            // 執行一個簡單的 PING 命令來測試連線是否正常
//            String pingResponse = connection.ping();
//            if ("PONG".equalsIgnoreCase(pingResponse)) {
//                log.info("Redis 連線正常");
//                return;
//            }
//        } catch (DataAccessException e) {
//            log.error("Redis 連線異常，嘗試重啟訂閱監聽器...", e);
//            restartRedisListener();
//        }
//    }
//
//    /**
//     * 重啟 Redis 訂閱監聽器
//     */
//    private void restartRedisListener() {
//        try {
//            redisMessageListenerContainer.stop();
//            redisMessageListenerContainer.start();
//            log.info("Redis 訂閱監聽器重新啟動成功");
//        } catch (Exception e) {
//            log.error("Redis 訂閱監聽器重啟失敗", e);
//        }
//    }
//}
