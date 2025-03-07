package org.example.lockproject.service;


import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import static org.example.lockproject.enums.TicketBookingEnums.SOME_TICKET;

@Service
@Log4j2
public class TicketBookingRedisService {

    @Resource
    private JedisPool jedisPool;

    @Resource
    private RedisService redisService;


    //存TicketToken 過期後代表沒有付款 key = ticketToken_userID_area_area
    public void setTicketTokenKey(String ticketToken, String userId, String area, Long expireTime){
        boolean set = redisService.set(ticketTokenKeyCreat(ticketToken,userId,area),ticketToken,expireTime);
        if(!set){
            log.error("setTicketToken err {ticketToken: {} userID: {}}", ticketToken,userId);
        }
    }

    //取的存TicketToken 緩存 key = ticketToken_userID_area
    //反回 取的存TicketToken
    public String GetTicketTokenValue(String ticketToken, String userId, String area){
        String ticketTokenValue = (String) redisService.get(ticketTokenKeyCreat(ticketToken,userId,area));
        if(StringUtils.isBlank(ticketTokenValue)){
            return "";
        }
        return ticketTokenValue;
    }

    public static String GetTicketTokenKeyToken(String ticketTokenKey){
        if (StringUtils.isBlank(ticketTokenKey)) {
            return "";
        }
        String[] split = StringUtils.split(ticketTokenKey,"_");
        if (split == null || split.length == 0 || StringUtils.isBlank(split[0])) {
            return "";
        }
        return split[0];
    }

    public static String GetTicketTokenKeyUserId(String ticketTokenKey) {
        if (StringUtils.isBlank(ticketTokenKey)) {
            return "";
        }
        String[] split = StringUtils.split(ticketTokenKey, "_");
        if (split == null || split.length < 2 || StringUtils.isBlank(split[1])) {
            return "";
        }
        return split[1];
    }

    public static String GetTicketTokenKeyArea(String ticketTokenKey){
        if (StringUtils.isBlank(ticketTokenKey)) {
            return "";
        }
        String[] split = StringUtils.split(ticketTokenKey,"_");
        if (split == null || split.length < 3 || StringUtils.isBlank(split[2])) {
            return "";
        }
        return split[2];
    }

    protected static String ticketTokenKeyCreat(String ticketToken,String userId,String area){
        return ticketToken+"_"+userId+"_"+area;
    }


    //------------------------------------------------------------

    public void setTicketKey(String ticketKey){
        Jedis jedis = jedisPool.getResource();
        try {
            if (jedis.get(ticketKey)== null ||jedis.get(ticketKey).isEmpty()) {
                jedis.set(ticketKey, "1000");
            }
        }finally {
            jedis.close();
        }
    }


    public boolean acquireLock(String lockValue) {
        Jedis jedis = jedisPool.getResource();
        String result ;
        try {
            SetParams params = new SetParams();
            params.nx().px(SOME_TICKET.expireTime);  // NX: 如果不存在則創建, PX: 以毫秒為單位設置過期時間
            result = jedis.set(SOME_TICKET.lockKey, lockValue, params);
        }finally {
            jedis.close();
        }
        return "OK".equals(result);
    }

    public boolean releaseLock(String lockValue) {
        Jedis jedis = jedisPool.getResource();
        Object result;
        try {
            // Lua 腳本：如果 Redis 中 key 對應的值與 lockValue 相同，則刪除該 key；否則不做操作
            String luaScript =
                    "if redis.call('get', KEYS[1]) == ARGV[1] then " + // 檢查 Redis 中指定 key 的值是否與傳入的 lockValue 相等
                            "return redis.call('del', KEYS[1]) " +            // 如果相等，執行 del 命令刪除這個 key
                            "else return 0 end";                             // 如果不相等，返回 0，表示未刪除

            // 執行 Lua 腳本，傳入的參數分別是：
            // - luaScript：要執行的 Lua 腳本
            // - 1：表示 KEYS 的數量，只有 1 個 key
            // - lockKey：傳入的 key 對應 Redis 中的鍵
            // - lockValue：傳入的 ARGV[1]，即與 Redis 中該鍵的值比較的字符串
            result = jedis.eval(luaScript, 1, SOME_TICKET.lockKey, lockValue);

            // 判斷執行結果是否為 1（Lua 腳本返回的值），1 表示成功刪除了 key，即釋放了鎖
        }finally {
            jedis.close();
        }

        return "1".equals(result.toString());
    }

    public String bookTicket(String lockValue) {
        Jedis jedis = jedisPool.getResource();
        try {
            if (acquireLock(lockValue)) {
                try {
                    int ticketCount = Integer.parseInt(jedis.get(SOME_TICKET.ticketKey));
                    if (ticketCount > 0) {
                        jedis.decr(SOME_TICKET.ticketKey);  // 扣減票數
                        return "搶票成功，剩餘票數：" + (ticketCount - 1);
                    } else {
                        return "票已售罄";
                    }
                } finally {
                    //這裡是釋放鎖 不是刪票
                    releaseLock(lockValue);
                }
            } else {
                return "系統繁忙，請稍後再試";
            }
        }finally {
            jedis.close();
        }
    }
}
