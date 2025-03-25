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
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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



    //驗證TOKEN sha256 ticketName_userId_area_book_time
    public StatusCode ticketTokenVerify(String ticketName , String userId, String area , String bookTime, String ticketToken) throws Exception {

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


    public void insertTicket(TicketDBTableEnums tableName , TicketDAO ticketDAO){
        ticketMapper.insertOne(tableName.getName() , ticketDAO);
    }

    public List<TicketDAO> selectTicketByUserId(TicketDBTableEnums ticketDBTableEnums, String userID){
        return ticketMapper.findByUserId(ticketDBTableEnums.getName(),userID);
    }

    public TicketDAO selectTicketByToken(TicketDBTableEnums ticketDBTableEnums, String token){
        try {
            return ticketMapper.findTicketByToken(ticketDBTableEnums.getName(),token);
        } catch (DataAccessException e) {  // 如果是數據庫訪問錯誤
            log.error("selectTicketByToken database error for token: {} , msg:{}", token, e.getMessage());
        } catch (Exception e) {  // 捕獲其他一般錯誤
            log.error("selectTicketByToken unexpected error for token: {} , msg:{}", token, e.getMessage());
        }
        return null;
    }


    public int selectTicketTypeByToken(TicketDBTableEnums tableName , String token){
        int type = 0;
        try {
            type = ticketMapper.findTicketTypeByToken(tableName.getName(),token);
        }catch (Exception e){
            log.error("selectNginxQATicketTypeByToken err token: {}", token);
        }
        return type;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ , rollbackFor = Exception.class)
    public boolean updateTicketType(TicketDBTableEnums tableName , String ticketToken , TicketType ticketType){
        int i = ticketMapper.updateTicketType(tableName.getName(), ticketToken, ticketType.tpye);
        if(i != 1){
            log.error("updateTicketType更新異常");
            return false;
        }
        System.out.println("更新行數:"+i);
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ , rollbackFor = Exception.class)
    public boolean checkAndUpdateTicketType(TicketDBTableEnums tableName,TicketType checkoutType , TicketType updateType , String token){
        TicketDAO dao = selectTicketByToken(tableName, token);
        if (dao == null) {
            log.error("沒有該Token在DB : {}", token);
            return false;
        }
        int ticketType = dao.getTicketType();
        TicketType dbTickettype = TicketType.parse(ticketType);
        if (dbTickettype != null && dbTickettype == checkoutType) {
            updateTicketType(tableName, token, updateType);
            return true;
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ , rollbackFor = Exception.class)
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
