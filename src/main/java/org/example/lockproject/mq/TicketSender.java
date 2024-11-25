package org.example.lockproject.mq;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import org.example.lockproject.exception.QueuesException;
import org.example.lockproject.exception.SomeException;
import org.springframework.amqp.core.AmqpTemplate;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Consumer;

import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import static org.example.lockproject.conf.RabbitMqConfig.QUEUEA_NAME;
import static org.example.lockproject.conf.RabbitMqConfig.QUEUEB_NAME;

@Component
public class TicketSender {


    @Resource
    private AmqpTemplate amqpTemplate;

    public void setQueues(String area,int ticketTotal) throws SomeException {
        Consumer<TicketResource> function = getFunction(area);
        for(int i = 1; i < ticketTotal +1 ; i++){
            TicketResource build = TicketResource.builder()
                    .id(i)
                    .area(area)
                    .build();
            function.accept(build);
        }

    }



//    public interface Function<T, R>
//    表示一個接受參數 T，返回結果 R 的函數

//    public interface Consumer<T>
//    表示一個接受參數 T
    private Consumer<TicketResource> getFunction(String area) throws SomeException {
        switch (area) {
            case QUEUEA_NAME:
                return this::sendTicketA;
            case QUEUEB_NAME:
                return this::sendTicketB;
            default:
                throw new SomeException("沒有對應 Queue");
        }
    }



    public void sendTicketA(TicketResource ticketResource) {
        try {
            String msg = JSON.toJSONString(ticketResource);
            System.out.println("發送 : " + msg);
            amqpTemplate.convertAndSend(QUEUEA_NAME, msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendTicketB(TicketResource ticketResource) {
        try {
            String msg = JSON.toJSONString(ticketResource);
            System.out.println("發送 : " + msg);
            amqpTemplate.convertAndSend(QUEUEB_NAME, msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public TicketResource bookTicketA(String queueName) throws QueuesException {
        TicketResource ticketResource = null;
        Message receive = amqpTemplate.receive(queueName, 500);
        if(receive != null){
            // 將字節數組轉換為字符串
            String json = new String(receive.getBody(), StandardCharsets.UTF_8);
            ticketResource = JSON.parseObject(json, TicketResource.class);
            //無法解析JSON
//        TicketResource objectFromBytes = (TicketResource)getObjectFromBytes(receive.getBody());
        }
        System.out.println(ticketResource);
        return ticketResource;
    }

    // 將字節碼轉換為對象
    public Object getObjectFromBytes(byte[] objBytes) throws Exception {
        if (objBytes == null || objBytes.length == 0) {
            return null;
        }
        // 使用 try-with-resources 確保流關閉
        try (ByteArrayInputStream bi = new ByteArrayInputStream(objBytes);
             ObjectInputStream oi = new ObjectInputStream(bi)) {

            return oi.readObject();
        }
    }



}
