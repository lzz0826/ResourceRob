package org.example.lockproject;


import jakarta.annotation.Resource;
import org.example.lockproject.dao.TicketDAO;
import org.example.lockproject.enums.TicketDBTableEnums;
import org.example.lockproject.enums.TicketType;
import org.example.lockproject.service.TicketBookingNginxDbService;
import org.example.lockproject.service.TicketBookingRedisService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.example.lockproject.enums.TicketBookingEnums.SOME_TICKET;

@SpringBootTest

public class TicketTokenTest {

    @Resource
    private TicketBookingRedisService ticketBookingRedisService;

    @Resource
    private TicketBookingNginxDbService ticketBookingNginxDbService;




    @Test
    public void test(){
        TicketDAO build = TicketDAO
                .builder()
                .ticketName("standard")
                .userId("user001")
                .area("nginxQA")
                .bookTime(1742893981L)
                .ticketToken("2af2046b130dafb5acedb13acdb3fe3f46124fd74009b53a6dcb91c74873af91")
                .ticketType(0)
                .updateTime(new Date())
                .build();
        ticketBookingNginxDbService.insertTicket(TicketDBTableEnums.NGINX_QA,build);
    }


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
