package org.example.lockproject.dao;


import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class TicketDAO {

  private Integer id;

  private String ticketId;

  private String userId;

  private String area;

  private Date time;

}
