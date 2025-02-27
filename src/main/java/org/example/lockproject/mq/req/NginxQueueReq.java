package org.example.lockproject.mq.req;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NginxQueueReq {

    private String ticketName;

    private String userId;

    private String area;

    private Date bookTime;

    private String ticketToken;


}
