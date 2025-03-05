package org.example.lockproject;


import jakarta.annotation.Resource;
import org.example.lockproject.service.TicketBookingRedisService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.example.lockproject.enums.TicketBookingEnums.SOME_TICKET;

@SpringBootTest

public class TicketTokenTest {

    @Resource
    private TicketBookingRedisService ticketBookingRedisService;

    @Test
    public void setTicketTokenTest(){
        ticketBookingRedisService.setTicketToken("testToken","testUserId",30L);
    }

    @Test
    public void getTicketTokenTest(){
//        public void setTicketToken(String ticketToken,String userId, Long expireTime){
        String testToken = ticketBookingRedisService.getTicketToken("testToken");
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
