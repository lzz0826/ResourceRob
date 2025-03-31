package org.example.lockproject;

import jakarta.annotation.Resource;
import org.example.lockproject.dao.TicketDAO;
import org.example.lockproject.mapper.TicketMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest

public class MapperTests {

    @Resource
    private TicketMapper ticketMapper;


    @Test
    public void findByUserIdTest(){
        List<TicketDAO> byUserId = ticketMapper.findByUserId("ticket_nginxQA","user001");

//        TODO 映射
        System.out.println(byUserId);

    }
}
