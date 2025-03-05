package org.example.lockproject.mq;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import jodd.util.StringUtil;
import org.example.lockproject.dao.TicketDAO;
import org.example.lockproject.enums.TicketTokenEnums;
import org.example.lockproject.mq.enums.NginxQueueEnums;
import org.example.lockproject.mq.req.NginxQueueReq;
import org.example.lockproject.service.TicketBookingNginxCacheService;
import org.example.lockproject.service.TicketBookingNginxDbService;
import org.example.lockproject.service.TicketBookingRedisService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;
import com.rabbitmq.client.Channel;


import java.util.Date;


@Component
@RabbitListener(queues = {"nginxQ","nginxQA", "nginxQB"})
public class NginxQReceiver {



  @Resource
  private TicketBookingNginxDbService ticketBookingNginxDbService;

  @Resource
  private TicketBookingRedisService ticketBookingRedisService;

  @RabbitHandler
  public void receive(byte[] messageBytes, Message message, Channel channel) {
    long deliveryTag = message.getMessageProperties().getDeliveryTag(); // 獲取 deliveryTag
    try {

      // 轉換消息
      String messageStr = new String(messageBytes);
      NginxQueueReq nginxQueueReq = JSON.parseObject(messageStr, NginxQueueReq.class);
      System.out.println("接收 : " + nginxQueueReq);

      NginxQueueEnums parse = NginxQueueEnums.parse(nginxQueueReq.getArea());

      if(parse == null){
        System.out.println("缺少區域參數");
        return;
      }

      if(StringUtil.isBlank(nginxQueueReq.getUserId()) ||  StringUtil.isBlank(nginxQueueReq.getArea())){
        System.out.println("少參數");
        return;
      }

      //TicketToken存入Redis過期代表沒有付款
      ticketBookingRedisService.setTicketToken(nginxQueueReq.getTicketToken(),nginxQueueReq.getUserId(), TicketTokenEnums.TICKET_TOKEN.expireTime);

      switch (parse) {
        case nginxQA:
          System.out.println("Set NginxQA");
//        ticketBookingNginxCacheService.setTicketQAMap(nginxQueueReq.getUserId(),nginxQueueReq);
          TicketDAO ta = builderTicket(nginxQueueReq);
          ticketBookingNginxDbService.insertQATicket(ta);
          break;
        case nginxQB:
          System.out.println("Set NginxQB");
//        ticketBookingNginxCacheService.setTicketQBMap(nginxQueueReq.getUserId(),nginxQueueReq);
          TicketDAO tb = builderTicket(nginxQueueReq);
          ticketBookingNginxDbService.insertQBTicket(tb);
          break;
        default:
          System.out.println("沒有對應區域");
          break;
      }

      // 手動 ACK
      channel.basicAck(deliveryTag, false);  // 確保消息處理成功後 ACK
      System.out.println("消息處理成功，ACK：" + deliveryTag);

    } catch (Exception e) {
      System.err.println("消息處理失敗：" + e.getMessage());
      try {
        //**手動 NACK，並決定是否重新入隊**
        channel.basicNack(deliveryTag, false, false); // false 代表不重新入隊
        System.out.println("消息處理失敗，NACK：" + deliveryTag);
      } catch (Exception nackEx) {
        System.err.println("NACK 失敗：" + nackEx.getMessage());
      }
    }
  }

  private TicketDAO builderTicket(NginxQueueReq nginxQueueReq){
      return TicketDAO.builder()
              .ticketName(nginxQueueReq.getTicketName())
              .userId(nginxQueueReq.getUserId())
              .area(nginxQueueReq.getArea())
              .bookTime(nginxQueueReq.getBookTime())
              .ticketToken(nginxQueueReq.getTicketToken())
              .updateTime(new Date())
              .build();
  }

}
