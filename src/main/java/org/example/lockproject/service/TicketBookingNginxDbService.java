package org.example.lockproject.service;


import jakarta.annotation.Resource;
import org.example.lockproject.dao.TicketDAO;
import org.example.lockproject.mapper.TicketMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TicketBookingNginxDbService {

    @Resource
    private TicketMapper ticketMapper;

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
