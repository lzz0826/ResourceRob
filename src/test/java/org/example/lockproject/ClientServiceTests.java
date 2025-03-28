package org.example.lockproject;


import jakarta.annotation.Resource;
import org.example.lockproject.client.obj.req.AddQuantityReq;
import org.example.lockproject.client.service.ClientService;
import org.example.lockproject.mq.enums.NginxQueueEnums;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ClientServiceTests {

    @Resource
    private ClientService clientService;

    @Test
    public void addQuantityReqTest(){
        AddQuantityReq addQuantityReq = clientService.addQuantityReq(NginxQueueEnums.nginxQA.name(),100);
        System.out.println("--------");
        System.out.println(addQuantityReq);
        System.out.println("--------");
    }


}
