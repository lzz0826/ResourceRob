package org.example.lockproject;

import jakarta.annotation.Resource;
import org.example.lockproject.dao.TicketDAO;
import org.example.lockproject.enums.TicketDBTableEnums;
import org.example.lockproject.exception.SomeException;
import org.example.lockproject.mapper.TicketMapper;
import org.example.lockproject.service.TicketBookingNginxDbService;
import org.example.lockproject.utils.SerializableUtil;
import org.example.lockproject.serializable.TicketRedisInfoSerializable;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.example.lockproject.conf.RabbitMqConfig.QUEUEA_NAME;
import static org.example.lockproject.conf.RabbitMqConfig.QUEUEB_NAME;

@SpringBootTest
class ResourceRobTests {


    @Resource
    private TicketBookingNginxDbService ticketBookingNginxDbService;




    @Test
    public void serializeTest() throws IOException, ClassNotFoundException {
        TicketRedisInfoSerializable t = new TicketRedisInfoSerializable("ticketName1","userId2","area3"
        ,"ticketToken4",new Date());
        byte[] serialize = SerializableUtil.serialize(t);

//        for (int i = 0; i < serialize.length; i++){
//            System.out.println(serialize[i]);
//        }
        TicketRedisInfoSerializable deserialize = (TicketRedisInfoSerializable) SerializableUtil.deserialize(serialize);
//        System.out.println(deserialize);
    }

    @Test
    public void testInsertTicket(){
        TicketDAO build = TicketDAO
                .builder()
                .ticketName("fff")
                .userId("userId")
                .area("a")
                .bookTime(new Date())
                .ticketToken("tbrtb")
                .ticketType(1)
                .updateTime(new Date())
                .build();

        ticketBookingNginxDbService.insertTicket(TicketDBTableEnums.NGINX_QA,build);
    }


    @Test
    public void testSelectTicketTypeByToken(){
        int i = ticketBookingNginxDbService.selectTicketTypeByToken(TicketDBTableEnums.NGINX_QA,"as");
        System.out.println(i);
    }

//    @Test
//    public void testUpdateTicketType(){
//        int i = ticketBookingNginxDbService.updateTicketType(TicketDBTableEnums.NGINX_QA,"afs",2);
//        System.out.println(i);
//    }


}
