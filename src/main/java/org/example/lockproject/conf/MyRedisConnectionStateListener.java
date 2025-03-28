package org.example.lockproject.conf;

import io.lettuce.core.RedisChannelHandler;
import io.lettuce.core.RedisConnectionStateListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class MyRedisConnectionStateListener implements RedisConnectionStateListener {


    @Override
    public void onRedisConnected(RedisChannelHandler<?, ?> connection) {
        log.info("Redis 連線成功");

    }
    @Override
    public void onRedisDisconnected(RedisChannelHandler<?, ?> connection) {
        log.warn("Redis 連線斷開");
    }

    @Override
    public void onRedisExceptionCaught(RedisChannelHandler<?, ?> connection, Throwable cause) {
        log.error("Redis 連線異常", cause);
    }

}
