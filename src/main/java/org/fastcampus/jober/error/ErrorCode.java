package org.fastcampus.jober.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 공통
    INTERNAL_SERVER_ERROR("COMMON-500", "서버 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("COMMON-400", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("COMMON-401", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("COMMON-403", "접근이 거부되었습니다.", HttpStatus.FORBIDDEN),
    NOT_FOUND("COMMON-404", "리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED("COMMON-405", "허용되지 않은 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),

    // 비즈니스 (예시)
    USER_NOT_FOUND("USER-404", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_RESOURCE("COMMON-409", "이미 존재하는 리소스입니다.", HttpStatus.CONFLICT),

    // 사용자 입력 검증
    INVALID_USERNAME("USER-400-01", "사용자명은 영문 소문자와 숫자 조합으로 5~15자여야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD("USER-400-02", "비밀번호는 영문, 숫자, 특수문자 조합으로 8~16자여야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL("USER-400-03", "올바른 이메일 형식이 아닙니다.", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXIST("USER-409-04", "이미 존재하는 이메일입니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}

