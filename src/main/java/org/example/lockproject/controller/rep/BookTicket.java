package org.example.lockproject.controller.rep;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookTicket {

    //UserId
    private String userId ;

    //有無搶到
    private boolean isGet;

    //區域
    private String area;

    //票ID
    private Integer ticketId;

}
