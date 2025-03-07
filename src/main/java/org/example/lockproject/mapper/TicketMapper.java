package org.example.lockproject.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.lockproject.dao.TicketDAO;


import java.util.List;


@Mapper
public interface TicketMapper {

    @Insert("INSERT INTO ticket_nginxQA (ticket_name, user_id, area, book_time, ticket_token, ticket_type,update_time) " +
            "VALUES (#{ticketName}, #{userId}, #{area}, #{bookTime}, #{ticketToken}, #{ticketType}, #{updateTime})")
    void insertNginxQAOne(TicketDAO ticket);

    @Insert("INSERT INTO ticket_nginxQB (ticket_name, user_id, area, book_time, ticket_token, ticket_type, update_time) " +
            "VALUES (#{ticketName}, #{userId}, #{area}, #{bookTime}, #{ticketToken}, #{ticketType}, #{updateTime})")
    void insertNginxQBOne(TicketDAO ticket);


    @Select("select *  From `ticket_nginxQA` where user_id = #{user_id}")
    List<TicketDAO> findNginxQAByUserId(String userId);

    @Select("select *  From `ticket_nginxQB` where user_id = #{user_id}")
    List<TicketDAO> findNginxQBByUserId(String userId);

    @Update("UPDATE ticket_nginxQA SET ticket_type = #{ticketType} WHERE ticket_token= #{ticketToken}")
    int updateNginxQATicketType(String ticketToken, int ticketType);

    @Update("UPDATE ticket_nginxQB SET ticket_type = #{ticketType} WHERE ticket_token= #{ticketToken}")
    int updateNginxQBTicketType(String ticketToken, int ticketType);



}
