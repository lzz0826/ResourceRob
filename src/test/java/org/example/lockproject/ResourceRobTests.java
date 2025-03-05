package org.example.lockproject;

import jakarta.annotation.Resource;
import org.example.lockproject.dao.TicketDAO;
import org.example.lockproject.mapper.TicketMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
class ResourceRobTests {

    @Resource
    private TicketMapper ticketMapper;


    @Test
    public void testFindNginxQAByUserId(){
        List<TicketDAO> nginxQAByUserId = ticketMapper.findNginxQAByUserId("123");
        System.out.println(nginxQAByUserId);
    }

    @Test
    public void testTicketDAO(){

        TicketDAO build = TicketDAO.builder()
                .ticketName("testName")
                .userId("123")
                .area("A")
                .bookTime(new Date())
                .ticketToken("testToken")
                .updateTime(new Date())
                .build();
        ticketMapper.insertNginxQAOne(build);

    }


}
