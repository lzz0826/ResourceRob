package org.example.lockproject.controller.req;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder
@ToString
public class CheckTicketTokenReq {

    @NotNull
    private String ticketName;

    @NotNull
    private String userId;

    @NotNull
    private String area;

    @NotNull
    private String ticketToken;

    @NotNull
    private String bookTime;

}
