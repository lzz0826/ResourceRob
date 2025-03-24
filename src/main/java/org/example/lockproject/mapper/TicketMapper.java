package org.example.lockproject.mapper;


import org.apache.ibatis.annotations.*;
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


    //TODO
    @Select("select *  From `ticket_nginxQA` where user_id = #{user_id}")
    List<TicketDAO> findNginxQAByUserId(String userId);

    @Select("select *  From `ticket_nginxQB` where user_id = #{user_id}")
    List<TicketDAO> findNginxQBByUserId(String userId);

    @Select("select ticket_type  From `${tableName}` where ticket_token = #{token}")
    int findNginxQATicketTypeByToken(@Param("tableName")String tableName, String token);

    @Update("UPDATE ${tableName} SET ticket_type = #{ticketType} WHERE ticket_token= #{ticketToken}")
    int updateTicketType(@Param("tableName")String tableName,String ticketToken, int ticketType);


}
