package org.example.lockproject.dao;


import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class TicketDAO {

  private Integer id;

  private String ticketName;

  private String userId;

  private String area;

  private Date bookTime;

  private String ticketToken;

  private int ticketType;

  private Date createTime;

  private Date updateTime;


}
