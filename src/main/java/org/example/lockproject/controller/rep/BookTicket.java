package org.example.lockproject.controller.rep;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookTicket {

    private String ticketName;

    //UserId
    private String userId ;

    //有無搶到
    private boolean isGet;

    //區域
    private String area;

    private Date bookTime;
    //票ID
    private String ticketToken;

}
