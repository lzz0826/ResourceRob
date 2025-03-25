package org.example.lockproject.dao;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class TicketDAO {

  @TableField("id")
  private Integer id;

  @TableField("ticket_name")
  private String ticketName;

  @TableField("user_id")
  private String userId;

  @TableField("area")
  private String area;

  @TableField("book_time")
  private Date bookTime;

  @TableField("ticket_token")
  private String ticketToken;

  @TableField("ticket_type")
  private int ticketType;

  @TableField("create_time")
  private Date createTime;

  @TableField("update_time")
  private Date updateTime;


}
