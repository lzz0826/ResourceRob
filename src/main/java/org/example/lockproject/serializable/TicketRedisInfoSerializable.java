package org.example.lockproject.serializable;

import java.io.Serializable;
import java.util.Date;


/**
 * Ticket 序列化 緩存Redis
 */
public class TicketRedisInfoSerializable implements Serializable {

    private String ticketName;
    private String userId;
    private String area;
    private String ticketToken;
    private Date bookTime;

    public TicketRedisInfoSerializable(String ticketName, String userId, String area, String ticketToken, Date bookTime) {
        this.ticketName = ticketName;
        this.userId = userId;
        this.area = area;
        this.ticketToken = ticketToken;
        this.bookTime = bookTime;
    }


    @Override
    public String toString() {
        return "TicketRedisInfoSerializable{" +
                "ticketName='" + ticketName + '\'' +
                ", userId='" + userId + '\'' +
                ", area='" + area + '\'' +
                ", ticketToken='" + ticketToken + '\'' +
                ", bookTime='" + bookTime + '\'' +
                '}';
    }
}
