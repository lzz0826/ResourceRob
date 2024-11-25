package org.example.lockproject.mq;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class TicketResource {

    //票 ID 目前代表第幾張
    private int id ;

    //區域
    private String area;


}
