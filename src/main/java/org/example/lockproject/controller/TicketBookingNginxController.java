package org.example.lockproject.controller;


import jakarta.annotation.Resource;
import jodd.util.StringUtil;
import org.example.lockproject.common.BaseResp;
import org.example.lockproject.common.StatusCode;
import org.example.lockproject.controller.rep.BookTicket;
import org.example.lockproject.dao.TicketDAO;
import org.example.lockproject.mq.req.NginxQueueReq;
import org.example.lockproject.service.TicketBookingNginxCacheService;
import org.example.lockproject.service.TicketBookingNginxDbService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/nginx")
public class TicketBookingNginxController {

    @Resource
    private TicketBookingNginxCacheService ticketBookingNginxCacheService;


    @Resource
    private TicketBookingNginxDbService ticketBookingNginxDbService;

    //------ 只有 Nginx ----------

    //鎖 和 資源(票) 做在Nginx JAVA只負責紀錄
    @GetMapping("/bookTicket")
    public BaseResp<Integer> bookTicket() {
        return BaseResp.ok(ticketBookingNginxCacheService.addCountTicket(), StatusCode.Success);
    }

    @GetMapping("/getCountTicket")
    public BaseResp<Integer> getCountTicket() {
        return BaseResp.ok(ticketBookingNginxCacheService.getCountTicket(), StatusCode.Success);
    }


    //------ Nginx + RabbitMa STOMP ----------

    //鎖 和 資源(票) 做在Nginx 搶到票對MQ發消息 JAVA負責 監聽 紀錄 查詢
    @GetMapping("/checkoutTicket/{userId}")
    public BaseResp<List<BookTicket>> checkoutTicket(@PathVariable("userId")String userId) {
        List<BookTicket> reps = new ArrayList<>();
        if(StringUtil.isBlank(userId)){
            return BaseResp.fail(reps, StatusCode.Success);
        }

        List<TicketDAO> ticketDAOS = ticketBookingNginxDbService.selectQATicketByUserId(userId);
        if(ticketDAOS != null){
            addRep(ticketDAOS,reps);
        }

        List<TicketDAO> ticketDBOS = ticketBookingNginxDbService.selectQBTicketByUserId(userId);
        if(ticketDBOS != null){
            addRep(ticketDBOS,reps);
        }

//        List<NginxQueueReq> qa = ticketBookingNginxCacheService.getTicketQAMap(userId);
//        if(qa != null){
//            reps.addAll(qa);
//        }
//        List<NginxQueueReq> qb = ticketBookingNginxCacheService.getTicketQBMap(userId);
//        if(qb != null){
//            reps.addAll(qb);
//        }

        return BaseResp.ok(reps, StatusCode.Success);
    }

    private void addRep(List<TicketDAO> tickets,List<BookTicket> reps){
        for (TicketDAO ticketDAO : tickets) {
            BookTicket nginxQueueReq = BookTicket.builder()
                    .ticketId(Integer.valueOf(ticketDAO.getTicketId()))
                    .userId(ticketDAO.getUserId())
                    .area(ticketDAO.getArea())
                    .isGet(true)
                    .build();
            reps.add(nginxQueueReq);
        }
    }

}
