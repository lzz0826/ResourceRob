package org.example.lockproject.controller.req;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@ToString
public class TicketToPayReq {

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
