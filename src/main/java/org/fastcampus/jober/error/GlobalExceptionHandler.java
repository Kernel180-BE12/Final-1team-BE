package org.fastcampus.jober.error;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  private ErrorResponse build(
      ErrorCode ec, String message, HttpServletRequest req, Map<String, Object> details) {
    return ErrorResponse.builder()
        .code(ec.getCode())
        .message(message != null ? message : ec.getMessage())
        .status(ec.getStatus().value())
        .path(req.getRequestURI())
        .requestId((String) req.getAttribute("requestId")) // 필터에서 넣어줄 예정 (없어도 null 허용)
        .timestamp(Instant.now())
        .details(details)
        .build();
  }

  // 비즈니스 예외
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusiness(
      BusinessException ex, HttpServletRequest req) {
    var ec = ex.getErrorCode();
    log.warn("[BUSINESS] {} - {}", ec.getCode(), ex.getMessage());
    return ResponseEntity.status(ec.getStatus()).body(build(ec, ex.getMessage(), req, null));
  }

  // 검증 오류
  @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
  public ResponseEntity<ErrorResponse> handleValidation(Exception ex, HttpServletRequest req) {
    var ec = ErrorCode.BAD_REQUEST;
    Map<String, Object> details = null;

    if (ex instanceof MethodArgumentNotValidException e) {
      details =
          e.getBindingResult().getFieldErrors().stream()
              .collect(
                  Collectors.toMap(
                      FieldError::getField,
                      DefaultMessageSourceResolvable::getDefaultMessage,
                      (a, o) -> a // 중복키 무시
                      ));
    } else if (ex instanceof BindException e) {
      details =
          e.getBindingResult().getFieldErrors().stream()
              .collect(
                  Collectors.toMap(
                      FieldError::getField,
                      DefaultMessageSourceResolvable::getDefaultMessage,
                      (a, o) -> a));
    }
    log.debug("[VALIDATION] {}", ex.getMessage());
    return ResponseEntity.status(ec.getStatus()).body(build(ec, null, req, details));
  }

  // 파라미터/타입/필수값
  @ExceptionHandler({
    MissingServletRequestParameterException.class,
    MethodArgumentTypeMismatchException.class
  })
  public ResponseEntity<ErrorResponse> handleParams(Exception ex, HttpServletRequest req) {
    var ec = ErrorCode.BAD_REQUEST;
    return ResponseEntity.status(ec.getStatus()).body(build(ec, ex.getMessage(), req, null));
  }

  //    // 보안
  //    @ExceptionHandler(AccessDeniedException.class)
  //    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex,
  // HttpServletRequest req) {
  //        var ec = ErrorCode.FORBIDDEN;
  //        return ResponseEntity.status(ec.getStatus())
  //                .body(build(ec, null, req, null));
  //    }

  // HTTP 관련
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
      HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
    var ec = ErrorCode.METHOD_NOT_ALLOWED;
    return ResponseEntity.status(ec.getStatus()).body(build(ec, ex.getMessage(), req, null));
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleMediaType(
      HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
    var ec = ErrorCode.BAD_REQUEST;
    return ResponseEntity.status(ec.getStatus()).body(build(ec, ex.getMessage(), req, null));
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(
      NoHandlerFoundException ex, HttpServletRequest req) {
    var ec = ErrorCode.NOT_FOUND;
    return ResponseEntity.status(ec.getStatus()).body(build(ec, ex.getMessage(), req, null));
  }

  // 그 외 (최종 캐치)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnknown(Exception ex, HttpServletRequest req) {
    var ec = ErrorCode.INTERNAL_SERVER_ERROR;
    log.error("[UNEXPECTED] ", ex);
    return ResponseEntity.status(ec.getStatus()).body(build(ec, null, req, null));
  }
}
