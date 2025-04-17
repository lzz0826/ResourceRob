package org.example.lockproject.client.obj.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddQuantityReq {

  private String addQuantity;

  private String area;

  private String message;

  private String status;

}
