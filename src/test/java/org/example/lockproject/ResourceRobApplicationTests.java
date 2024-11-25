package org.example.lockproject;

import jakarta.annotation.Resource;
import org.example.lockproject.dao.TicketDAO;
import org.example.lockproject.mapper.TicketMapper;
import org.example.lockproject.service.TicketBookingRedisService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.example.lockproject.enums.TicketBookingEnums.SOME_TICKET;

@SpringBootTest
class ResourceRobApplicationTests {

    @Resource
    private TicketBookingRedisService ticketBookingService;

    @Resource
    private TicketMapper ticketMapper;


    @Test
    public void setTicketKey(){
        ticketBookingService.setTicketKey(SOME_TICKET.ticketKey);
    }


    @Test
    public void bookTicket(){
        ticketBookingService.bookTicket(SOME_TICKET.lockKey);
    }

    @Test
    public void testTicketDAO(){

        TicketDAO build = TicketDAO.builder()
                .ticketId("1")
                .userId("123")
                .area("A")
                .time(new Date())
                .build();

        ticketMapper.insertNginxQAOne(build);



    }


}
