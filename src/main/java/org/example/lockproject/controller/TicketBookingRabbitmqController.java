package org.example.lockproject.controller;


import jakarta.annotation.Resource;
import org.example.lockproject.common.BaseResp;
import org.example.lockproject.common.StatusCode;
import org.example.lockproject.controller.rep.BookTicket;
import org.example.lockproject.controller.req.SetQueuesReq;
import org.example.lockproject.exception.SomeException;
import org.example.lockproject.mq.TicketResource;
import org.example.lockproject.mq.TicketSender;
import org.springframework.web.bind.annotation.*;

import static org.example.lockproject.conf.RabbitMqConfig.QUEUEA_NAME;
import static org.example.lockproject.conf.RabbitMqConfig.QUEUEB_NAME;


@RestController
@RequestMapping("/rabbitmq")
public class TicketBookingRabbitmqController {

    @Resource
    private TicketSender ticketSender;


    //向Ａ設置票
    @PostMapping("/setQa")
    public BaseResp<String> setQa(@RequestBody SetQueuesReq setQueuesReq) throws SomeException {
        Integer ticketTotal = setQueuesReq.ticketTotal;
        if (ticketTotal == null || ticketTotal < 0){
            return BaseResp.fail("參數少",StatusCode.MissingParameter);
        }
        ticketSender.setQueues(QUEUEA_NAME,ticketTotal);
        return BaseResp.ok(null, StatusCode.Success);
    }


    //向Ａ設置票
    @PostMapping("/setQb")
    public BaseResp<String> setQb(@RequestBody SetQueuesReq setQueuesReq) throws SomeException {
        Integer ticketTotal = setQueuesReq.ticketTotal;
        if (ticketTotal == null || ticketTotal < 0){
            return BaseResp.fail("參數少",StatusCode.MissingParameter);
        }
        ticketSender.setQueues(QUEUEB_NAME,ticketTotal);
        return BaseResp.ok(null, StatusCode.Success);
    }


    //向Ａ搶一張票
    @GetMapping("/bookTicketA")
    public BaseResp<BookTicket> bookTicketA(String userId) throws Exception {
        TicketResource ticketResource = ticketSender.bookTicketA(QUEUEA_NAME);
        return bookTicket(ticketResource,userId);
    }


    //向B搶一張票
    @GetMapping("/bookTicketB")
    public BaseResp<BookTicket> bookTicketB(String userId) throws Exception {
        TicketResource ticketResource = ticketSender.bookTicketA(QUEUEB_NAME);
        return bookTicket(ticketResource,userId);
    }

    private BaseResp<BookTicket> bookTicket(TicketResource ticketResource,String userId){
        if(ticketResource == null){
            BookTicket rep = BookTicket
                    .builder()
                    .userId(userId)
                    .isGet(false)
                    .build();
            return BaseResp.fail(rep,StatusCode.QueuesError);
        }
        //搶到可以近DB
        BookTicket rep = BookTicket
                .builder()
                .userId(userId)
                .isGet(true)
                .area(ticketResource.getArea())
//                .ticketId(ticketResource.getId())
                .build();
        return BaseResp.ok(rep, StatusCode.Success);
    }



}
