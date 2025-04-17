package org.example.lockproject.controller;


import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jodd.util.StringUtil;
import org.example.lockproject.common.BaseResp;
import org.example.lockproject.common.StatusCode;
import org.example.lockproject.controller.rep.BookTicket;
import org.example.lockproject.controller.rep.CheckoutTicketTokenRep;
import org.example.lockproject.controller.rep.TicketToPayedRep;
import org.example.lockproject.controller.req.CheckTicketTokenReq;
import org.example.lockproject.controller.req.TicketToPayReq;
import org.example.lockproject.dao.TicketDAO;
import org.example.lockproject.enums.TicketDBTableEnums;
import org.example.lockproject.service.TicketBookingNginxCacheService;
import org.example.lockproject.service.TicketBookingNginxDbService;
import org.example.lockproject.service.TicketBookingNginxService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/nginx")
public class TicketBookingNginxController {

    @Resource
    private TicketBookingNginxService ticketBookingNginxService;

    @Resource
    private TicketBookingNginxCacheService ticketBookingNginxCacheService;


    @Resource
    private TicketBookingNginxDbService ticketBookingNginxDbService;
    //------ Nginx + RabbitMa STOMP ----------
    //鎖 和 資源(票) 做在Nginx 搶到票對MQ發消息 JAVA負責 監聽 紀錄 查詢


    //檢查TicketToken是否過期
    @PostMapping("/checkoutTicketToken")
    public BaseResp<CheckoutTicketTokenRep> checkoutTicketToken(@RequestBody @Valid CheckTicketTokenReq req) throws Exception {

        String ticketName = req.getTicketName();
        String userId = req.getUserId();
        String area = req.getArea();
        String bookTime = req.getBookTime();
        String ticketToken = req.getTicketToken();

        StatusCode statusCode = ticketBookingNginxService.ticketTokenVerify(ticketName, userId,area, bookTime, ticketToken);

        CheckoutTicketTokenRep rep = CheckoutTicketTokenRep
                .builder()
                .ticketName(ticketName)
                .userId(userId)
                .ticketToken(ticketToken)
                .massage(statusCode.msg)
                .build();
        return BaseResp.ok(rep, statusCode);
    }

    @GetMapping("/checkoutTicket/{userId}")
    public BaseResp<List<BookTicket>> checkoutTicket(@PathVariable("userId")String userId) {
        List<BookTicket> reps = new ArrayList<>();
        if(StringUtil.isBlank(userId)){
            return BaseResp.fail(reps, StatusCode.Success);
        }

        List<TicketDAO> ticketDAOS = ticketBookingNginxDbService.selectTicketByUserId(TicketDBTableEnums.NGINX_QA,userId);
        if(ticketDAOS != null){
            addRep(ticketDAOS,reps);
        }

        List<TicketDAO> ticketDBOS = ticketBookingNginxDbService.selectTicketByUserId(TicketDBTableEnums.NGINX_QB,userId);
        if(ticketDBOS != null){
            addRep(ticketDBOS,reps);
        }
        return BaseResp.ok(reps, StatusCode.Success);
    }


    //付款API 狀態 TICKET_PAYING -> TICKET_IS_PAY
    @PostMapping("/ticketToPayed")
    public BaseResp<TicketToPayedRep> ticketToPayed(@RequestBody @Valid TicketToPayReq req) throws Exception {

        TicketToPayedRep rep = new TicketToPayedRep();

        TicketDAO ticketDAO = ticketBookingNginxService.ticketToPayed(req.getTicketName(),req.getUserId(),req.getArea(),req.getBookTime(),req.getTicketToken());
        rep.setUserId(ticketDAO.getUserId());
        rep.setTicketName(ticketDAO.getTicketName());
        rep.setTicketToken(ticketDAO.getTicketToken());
        rep.setArea(ticketDAO.getArea());

        return BaseResp.ok(rep, ticketDAO.getStatusCode());

    }



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



    private void addRep(List<TicketDAO> tickets,List<BookTicket> reps){
        for (TicketDAO ticketDAO : tickets) {
            BookTicket nginxQueueReq = BookTicket.builder()
                    .ticketName(ticketDAO.getTicketName())
                    .userId(ticketDAO.getUserId())
                    .area(ticketDAO.getArea())
                    .bookTime(ticketDAO.getBookTime())
                    .ticketToken(ticketDAO.getTicketToken())
                    .isGet(true)
                    .build();
            reps.add(nginxQueueReq);
        }
    }

}
