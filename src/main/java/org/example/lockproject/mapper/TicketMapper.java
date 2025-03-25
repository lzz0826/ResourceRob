package org.example.lockproject.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.lockproject.dao.TicketDAO;


import java.util.List;


@Mapper
public interface TicketMapper extends BaseMapper<TicketDAO> {

    @Insert("INSERT INTO ${tableName} (ticket_name, user_id, area, book_time, ticket_token, ticket_type, update_time) " +
            "VALUES (#{ticketDao.ticketName}, #{ticketDao.userId}, #{ticketDao.area}, #{ticketDao.bookTime}, #{ticketDao.ticketToken}, #{ticketDao.ticketType}, #{ticketDao.updateTime})")
    void insertOne(@Param("tableName")String tableName, @Param("ticketDao") TicketDAO ticketDao);

    @Select("select *  From `${tableName}` where user_id = #{user_id}")
    List<TicketDAO> findByUserId(@Param("tableName")String tableName, String userId);

    @Select("select * From `${tableName}` where ticket_token = #{token}")
    TicketDAO findTicketByToken(@Param("tableName")String tableName, String token);


    @Select("select ticket_type  From `${tableName}` where ticket_token = #{token}")
    int findTicketTypeByToken(@Param("tableName")String tableName, String token);

    @Update("UPDATE ${tableName} SET ticket_type = #{ticketType} WHERE ticket_token= #{ticketToken}")
    int updateTicketType(@Param("tableName")String tableName,String ticketToken, int ticketType);


}
