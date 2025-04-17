package org.example.lockproject.client.obj;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Results<T> {

  private int code;

  private String status;

  private T data;

}
