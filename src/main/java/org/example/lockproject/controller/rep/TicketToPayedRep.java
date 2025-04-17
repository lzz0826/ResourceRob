package org.example.lockproject.controller.rep;


import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class TicketToPayedRep {

    private String ticketName;

    //UserId
    private String userId ;

    //區域
    private String area;

    //票ID
    private String ticketToken;

}
