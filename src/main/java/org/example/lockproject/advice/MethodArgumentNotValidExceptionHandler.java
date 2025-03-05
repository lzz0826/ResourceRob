package org.example.lockproject.advice;

import lombok.extern.log4j.Log4j2;
import org.example.lockproject.common.BaseResp;
import org.example.lockproject.common.StatusCode;
import org.example.lockproject.exception.QueuesException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class QueuesExceptionHandler {

  @org.springframework.web.bind.annotation.ExceptionHandler(QueuesException.class)
  public BaseResp<?> handleException(QueuesException ex){
    return BaseResp.fail(StatusCode.QueuesError);
  }

}
