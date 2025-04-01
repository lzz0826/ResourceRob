package org.example.lockproject.dao;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import org.example.lockproject.common.StatusCode;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
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
  private Long bookTime;

  @TableField("ticket_token")
  private String ticketToken;

  @TableField("ticket_type")
  private int ticketType;

  @TableField("create_time")
  private Date createTime;

  @TableField("update_time")
  private Date updateTime;

  @TableField(exist = false) //ORM 會忽略這個欄位
  private StatusCode statusCode;


  public Date getBookTimeAsDate() {
    return bookTime == null ? null : new Date(bookTime * 1000); // 秒級 Unix Timestamp，需要轉毫秒
  }

  public void setBookTimeFromDate(Date date) {
    this.bookTime = (date == null) ? null : date.getTime() / 1000; // 轉換成秒級 Unix Timestamp
  }



}
