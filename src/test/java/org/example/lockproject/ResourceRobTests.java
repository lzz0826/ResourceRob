package org.example.lockproject;

import jakarta.annotation.Resource;
import org.example.lockproject.dao.TicketDAO;
import org.example.lockproject.mapper.TicketMapper;
import org.example.lockproject.utils.SerializableUtil;
import org.example.lockproject.serializable.TicketRedisInfoSerializable;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@SpringBootTest
class ResourceRobTests {

    @Resource
    private TicketMapper ticketMapper;


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
