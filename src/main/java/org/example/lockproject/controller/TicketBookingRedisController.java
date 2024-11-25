package org.example.lockproject.controller;


import jakarta.annotation.Resource;
import org.example.lockproject.common.BaseResp;
import org.example.lockproject.common.StatusCode;
import org.example.lockproject.service.TicketBookingRedisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.example.lockproject.enums.TicketBookingEnums.SOME_TICKET;


@RestController
@RequestMapping("/ticket")
public class TicketBookingRedisController {

    @Resource
    private TicketBookingRedisService ticketBookingService;

    @GetMapping("/ticketInit")
    public BaseResp<String> ticketInit() {
        ticketBookingService.setTicketKey(SOME_TICKET.ticketKey);
        return BaseResp.ok("", StatusCode.Success);
    }

    @GetMapping("/bookTicket")
    public BaseResp<String> bookTicket() {
        String s = ticketBookingService.bookTicket(SOME_TICKET.lockKey);
        return BaseResp.ok(s, StatusCode.Success);
    }




}
