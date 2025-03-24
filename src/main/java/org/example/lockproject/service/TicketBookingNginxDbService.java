package org.example.lockproject.service;


import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.example.lockproject.common.StatusCode;
import org.example.lockproject.dao.TicketDAO;
import org.example.lockproject.enums.TicketDBTableEnums;
import org.example.lockproject.enums.TicketType;
import org.example.lockproject.mapper.TicketMapper;
import org.example.lockproject.mq.enums.NginxQueueEnums;
import org.example.lockproject.utils.HMACSHA256Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Log4j2
public class TicketBookingNginxDbService {

    @Resource
    private TicketMapper ticketMapper;


    @Resource
    private RedisService redisService;

    @Resource
    private TicketBookingRedisService ticketBookingRedisService;

    @Value("${ticket.signKey}")
    private String signKey;


    public StatusCode ticketTokenProcess(String ticketName ,String userId,String area ,String bookTime,String ticketToken) throws Exception {

        String token = ticketBookingRedisService.GetTicketTokenValue(ticketToken,userId,area);

        if(StringUtils.isBlank(token)){
            return StatusCode.TicketTokenTimeOutError;
        }

        //驗證簽名
        if(!ticketTokenSign(ticketName,userId,area ,bookTime,token)){
            return StatusCode.TicketTokenSignError;
        }
        return StatusCode.Success;
    }

    public boolean ticketTokenSign(String ticketName ,String userId,String area ,String bookTime,String ticketToken) throws Exception {
        //簽名規則 ticketName_userId_area_bookTime
        String signStr = ticketName + "_" + userId + "_" + area +"_" + bookTime;
        String key = signKey;
        String checkKey = HMACSHA256Util.generateHmacSHA256(key,signStr);
        return ticketToken.equals(checkKey);
    }

    public void insertQATicket(TicketDAO ticketDAO){
        ticketMapper.insertNginxQAOne(ticketDAO);
    }

    public void insertQBTicket(TicketDAO ticketDAO){
        ticketMapper.insertNginxQBOne(ticketDAO);
    }

    public List<TicketDAO> selectQATicketByUserId(String userID){
        return ticketMapper.findNginxQAByUserId(userID);
    }

    public List<TicketDAO> selectQBTicketByUserId(String userID){
        return ticketMapper.findNginxQBByUserId(userID);
    }

    public int selectTicketTypeByToken(TicketDBTableEnums tableName , String token){
        int type = 0;
        try {
            type = ticketMapper.findNginxQATicketTypeByToken(tableName.getName(),token);
        }catch (Exception e){
            log.error("selectNginxQATicketTypeByToken err {token: {}}", token);
        }
        return type;
    }


    public boolean updateTicketType(TicketDBTableEnums tableName , String ticketToken , TicketType ticketType){
        int i = ticketMapper.updateTicketType(tableName.getName(), ticketToken, ticketType.tpye);
        if(i != 1){
            log.error("updateTicketType更新異常");
            return false;
        }
        System.out.println("更新行數:"+i);
        return true;
    }

    public boolean updateNginxTicketType(String ticketToken , TicketType ticketType, String area){
        NginxQueueEnums parse = NginxQueueEnums.parse(area);
        switch (parse) {
            case nginxQA:
                return updateTicketType(TicketDBTableEnums.NGINX_QA,ticketToken,ticketType);
            case nginxQB:
                return updateTicketType(TicketDBTableEnums.NGINX_QB,ticketToken,ticketType);
        }
        log.error("updateNginxTicketType 沒有該區域 area:"+ area);
        return false;
    }
}
