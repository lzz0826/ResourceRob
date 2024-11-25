package org.example.lockproject.test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class TicketRedisBookingSystem {
    private Jedis jedis;
    private String lockKey;
    private String ticketKey = "ticket_count";
    private int expireTime;

    public TicketRedisBookingSystem(Jedis jedis, String lockKey, int expireTime) {
        this.jedis = jedis;
        this.lockKey = lockKey;
        this.expireTime = expireTime;
        // 初始化票數 1000 張
        setTicketKey();
    }

    public void setTicketKey(){
        if (jedis.get(ticketKey)== null ||jedis.get(ticketKey).isEmpty()) {
            jedis.set(ticketKey, "1000");
        }
    }

    public boolean acquireLock(String lockValue) {
        SetParams params = new SetParams();
        params.nx().px(expireTime);  // NX: 如果不存在則創建, PX: 以毫秒為單位設置過期時間
        String result = jedis.set(lockKey, lockValue, params);
        return "OK".equals(result);
    }

    public boolean releaseLock(String lockValue) {
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
        Object result = jedis.eval(luaScript, 1, lockKey, lockValue);

        // 判斷執行結果是否為 1（Lua 腳本返回的值），1 表示成功刪除了 key，即釋放了鎖
        return "1".equals(result.toString());
    }

    public String bookTicket(String lockValue) {
        if (acquireLock(lockValue)) {
            try {
                int ticketCount = Integer.parseInt(jedis.get(ticketKey));
                if (ticketCount > 0) {
                    jedis.decr(ticketKey);  // 扣減票數
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
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost", 6379);
        jedis.auth("123456");
        TicketRedisBookingSystem bookingSystem = new TicketRedisBookingSystem(jedis, "ticket_lock", 5000);

        String lockValue = "uniqueLockId";
        System.out.println(bookingSystem.bookTicket(lockValue));

        TicketRedisBookingSystem bookingSystem3 = new TicketRedisBookingSystem(jedis, "ticket_lock", 5000);
        System.out.println(bookingSystem3.bookTicket(lockValue));
        System.out.println(bookingSystem.bookTicket(lockValue));


    }
}
