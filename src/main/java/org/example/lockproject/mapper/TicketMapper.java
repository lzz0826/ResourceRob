package org.example.lockproject.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.lockproject.dao.TicketDAO;
import org.springframework.stereotype.Service;

import java.util.List;


@Mapper
public interface TicketMapper {

    @Insert("INSERT INTO ticket_nginxQA (ticketId, userId, area, time) " +
            "VALUES (#{ticketId}, #{userId}, #{area}, #{time})")
    void insertNginxQAOne(TicketDAO ticket);

    @Insert("INSERT INTO ticket_nginxQB (ticketId, userId, area, time) " +
            "VALUES (#{ticketId}, #{userId}, #{area}, #{time})")
    void insertNginxQBOne(TicketDAO ticket);


    @Select("select * ,time From `ticket_nginxQA` where userId = #{userId}")
    List<TicketDAO> findNginxQAByUserId(String userId);

    @Select("select * ,time From `ticket_nginxQB` where userId = #{userId}")
    List<TicketDAO> findNginxQBByUserId(String userId);



}
