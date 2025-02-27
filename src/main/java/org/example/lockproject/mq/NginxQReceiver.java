package org.example.lockproject.mq;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import jodd.util.StringUtil;
import org.example.lockproject.dao.TicketDAO;
import org.example.lockproject.mq.enums.NginxQueueEnums;
import org.example.lockproject.mq.req.NginxQueueReq;
import org.example.lockproject.service.TicketBookingNginxCacheService;
import org.example.lockproject.service.TicketBookingNginxDbService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
@RabbitListener(queues = {"nginxQ","nginxQA", "nginxQB"})
public class NginxQReceiver {

  @Resource
  private TicketBookingNginxCacheService ticketBookingNginxCacheService;

  @Resource
  private TicketBookingNginxDbService ticketBookingNginxDbService;

  @RabbitHandler
  public void receive(byte[] messageBytes) {

    // 將字節數組轉換為字符串
    String message = new String(messageBytes);

    NginxQueueReq nginxQueueReq = JSON.parseObject(message, NginxQueueReq.class);
    System.out.println("接收 : "+nginxQueueReq);
    NginxQueueEnums parse = NginxQueueEnums.parse(nginxQueueReq.getArea());

    if(parse == null){
      System.out.println("缺少區域參數");
      return;
    }

    if(StringUtil.isBlank(nginxQueueReq.getUserId()) ||  StringUtil.isBlank(nginxQueueReq.getArea())){
      System.out.println("少參數");
      return;
    }

    switch (parse) {
      case NginxQA:
        System.out.println("Set NginxQA");
//        ticketBookingNginxCacheService.setTicketQAMap(nginxQueueReq.getUserId(),nginxQueueReq);
        TicketDAO ta = builderTicket(nginxQueueReq);
        ticketBookingNginxDbService.insertQATicket(ta);
        break;
      case NginxQB:
        System.out.println("Set NginxQB");
//        ticketBookingNginxCacheService.setTicketQBMap(nginxQueueReq.getUserId(),nginxQueueReq);
        TicketDAO tb = builderTicket(nginxQueueReq);
        ticketBookingNginxDbService.insertQBTicket(tb);
        break;
      default:
        System.out.println("沒有對應區域");
        break;
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
