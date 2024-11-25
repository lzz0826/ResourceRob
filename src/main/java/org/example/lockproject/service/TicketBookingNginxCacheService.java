package org.example.lockproject.service;


import org.example.lockproject.mq.req.NginxQueueReq;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TicketBookingNginxCacheService {


    //------- 只有Nginx -----------

    private static int countTicket = 0;

    public int addCountTicket(){
        countTicket++;
        System.out.println(countTicket);
        return countTicket;
    }

    public int getCountTicket(){
        return countTicket;
    }


    //------- Nginx + RabbitMq -----------

    /**
     * key:       value:
     * userID     List<NginxQueueReq>
     */
    public final static Map<String, List<NginxQueueReq>> ticketQAMap = new ConcurrentHashMap<>();
    public List<NginxQueueReq> getTicketQAMap(String key) {
        return ticketQAMap.getOrDefault(key, null);
    }

    public void setTicketQAMap(String key, NginxQueueReq value) {
        // 使用 ConcurrentHashMap 的 computeIfAbsent 方法來避免同步塊中的競爭條件
        ticketQAMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }


    public final static Map<String,List<NginxQueueReq>> ticketQBMap = new ConcurrentHashMap<>();
    /**
     * key:       value:
     * userID     List<NginxQueueReq>
     */
    public List<NginxQueueReq> getTicketQBMap(String key) {
        return ticketQBMap.getOrDefault(key, null);
    }
    public void setTicketQBMap(String key, NginxQueueReq value) {
        // 使用 ConcurrentHashMap 的 computeIfAbsent 方法來避免同步塊中的競爭條件
        ticketQBMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

}
