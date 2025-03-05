package org.example.lockproject.service;


import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.example.lockproject.common.StatusCode;
import org.example.lockproject.dao.TicketDAO;
import org.example.lockproject.mapper.TicketMapper;
import org.example.lockproject.utils.HMACSHA256Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
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

        String ticketTokenRedis = ticketBookingRedisService.getTicketToken(ticketToken);

        if(StringUtils.isBlank(ticketTokenRedis)){
            return StatusCode.TicketTokenTimeOutError;
        }
        //驗證簽名
        if(!ticketTokenSign(ticketName,userId,area ,bookTime,ticketToken)){
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




}
