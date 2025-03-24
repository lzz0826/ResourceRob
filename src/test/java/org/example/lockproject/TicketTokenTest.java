package org.example.lockproject;


import jakarta.annotation.Resource;
import org.example.lockproject.enums.TicketType;
import org.example.lockproject.service.TicketBookingNginxDbService;
import org.example.lockproject.service.TicketBookingRedisService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.example.lockproject.enums.TicketBookingEnums.SOME_TICKET;

@SpringBootTest

public class TicketTokenTest {

    @Resource
    private TicketBookingRedisService ticketBookingRedisService;

    @Resource
    private TicketBookingNginxDbService ticketBookingNginxDbService;


    @Test
    public void setTicketTokenTest(){
        ticketBookingRedisService.setTicketTokenKey("testToken","testUserId","QA",30L);
    }

    @Test
    public void getTicketTokenTest(){
//        public void setTicketToken(String ticketToken,String userId, Long expireTime){
        String testToken = ticketBookingRedisService.GetTicketTokenValue("testToken","user","area");
        System.out.println(testToken);
    }



    @Test
    public void setTicketKey(){
        ticketBookingRedisService.setTicketKey(SOME_TICKET.ticketKey);
    }


    @Test
    public void bookTicket(){
        ticketBookingRedisService.bookTicket(SOME_TICKET.lockKey);
    }
}
