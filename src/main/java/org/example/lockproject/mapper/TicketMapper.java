package org.example.lockproject.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.lockproject.dao.TicketDAO;
import org.springframework.stereotype.Service;

import java.util.List;


@Mapper
public interface TicketMapper {

    @Insert("INSERT INTO ticket_nginxQA (ticket_name, user_id, area, book_time, ticket_token,update_time) " +
            "VALUES (#{ticketName}, #{userId}, #{area}, #{bookTime}, #{ticketToken}, #{updateTime})")
    void insertNginxQAOne(TicketDAO ticket);

    @Insert("INSERT INTO ticket_nginxQB (ticket_name, user_id, area, book_time, ticket_token,update_time) " +
            "VALUES (#{ticketName}, #{userId}, #{area}, #{bookTime}, #{ticketToken}, #{updateTime})")
    void insertNginxQBOne(TicketDAO ticket);


    @Select("select *  From `ticket_nginxQA` where user_id = #{user_id}")
    List<TicketDAO> findNginxQAByUserId(String userId);

    @Select("select *  From `ticket_nginxQB` where user_id = #{user_id}")
    List<TicketDAO> findNginxQBByUserId(String userId);



}
