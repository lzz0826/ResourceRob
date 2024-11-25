package org.example.lockproject.controller.req;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetQueuesReq {

    //區域總票數
    public Integer ticketTotal;

}
