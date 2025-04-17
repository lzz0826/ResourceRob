package org.example.lockproject.controller.rep;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CheckoutTicketTokenRep {

    private String ticketName;

    private String userId;

    private String ticketToken;

    private String massage;

}
