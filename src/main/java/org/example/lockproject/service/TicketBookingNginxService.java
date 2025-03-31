package org.example.lockproject.service;


import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.example.lockproject.common.StatusCode;
import org.example.lockproject.dao.TicketDAO;
import org.example.lockproject.enums.TicketDBTableEnums;
import org.example.lockproject.enums.TicketType;
import org.example.lockproject.utils.HMACSHA256Util;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TicketBookingNginxService {

    @Value("${ticket.signKey}")
    private String signKey;

    @Resource
    private TicketBookingNginxDbService dbService;

    @Resource
    private TicketBookingRedisService ticketBookingRedisService;

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

    //付款API 狀態 TICKET_PAYING -> TICKET_IS_PAY
    public TicketDAO ticketToPayed(String ticketName ,String userId,String area ,String bookTime,String ticketToken) throws Exception {
        TicketDBTableEnums ticketDBTableEnums = TicketDBTableEnums.areaParse(area);

//        TicketDAO repDao = new TicketDAO();
        TicketDAO repDao = TicketDAO.builder().build();
        if(ticketDBTableEnums == null){
            repDao.setStatusCode(StatusCode.TicketNotHasAreaError);
            return repDao;
        }
        //驗證 Token
        boolean b = ticketTokenSign(ticketName,userId,area ,bookTime,ticketToken);
        if(!b){
            repDao.setStatusCode(StatusCode.TicketTokenSignError);
            return repDao;
        }
        TicketDAO ticketDAO = dbService.selectTicketByToken(ticketDBTableEnums,ticketToken);
        if(ticketDAO == null){
            repDao.setStatusCode(StatusCode.NotHasTicketRecordError);
            return repDao;
        }

        BeanUtils.copyProperties(ticketDAO,repDao);

        int ticketType = ticketDAO.getTicketType();
        if(TicketType.TICKET_PAYING.tpye != ticketType){
            repDao.setStatusCode(StatusCode.TicketTypeNotPayingError);
            return repDao;
        }
        boolean up = dbService.checkAndUpdateTicketType(ticketDBTableEnums,TicketType.TICKET_PAYING,TicketType.TICKET_IS_PAY,ticketToken);
        if(up){
            repDao.setStatusCode(StatusCode.Success);
            return repDao;
        }
        repDao.setStatusCode(StatusCode.TicketToPayedError);
        return repDao;

    }


}
