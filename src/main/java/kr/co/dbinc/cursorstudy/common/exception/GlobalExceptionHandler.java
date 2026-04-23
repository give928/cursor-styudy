package kr.co.dbinc.cursorstudy.common.exception;

import javax.servlet.http.HttpServletRequest;

import kr.co.dbinc.cursorstudy.common.web.ProblemDetail;
import kr.co.dbinc.cursorstudy.domain.claim.exception.ClaimNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * REST API 전역 예외 처리.
 *
 * <p>정상 응답은 {@link kr.co.dbinc.cursorstudy.common.web.CommonResponse}를 사용하고,
 * 예외 응답은 RFC 7807 규격의 {@link ProblemDetail}을 반환한다.</p>
 */
@Slf4j
@RestControllerAdvice(basePackages = "kr.co.dbinc.cursorstudy")
public class GlobalExceptionHandler {

    /**
     * 존재하지 않는 클레임 조회 요청을 처리한다.
     *
     * @param exception 발생 예외
     * @param request   현재 HTTP 요청
     * @return 404 + RFC 7807 ProblemDetail
     */
    @ExceptionHandler(ClaimNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleClaimNotFound(ClaimNotFoundException exception,
                                                             HttpServletRequest request) {
        log.warn("Claim not found: {}", exception.getMessage());
        int statusCode = HttpStatus.NOT_FOUND.value();
        ProblemDetail body = ProblemDetail
                .of(statusCode, "CLAIM_001")
                .title("Claim Not Found")
                .detail(exception.getMessage())
                .instance(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body);
    }

    /**
     * 잘못된 도메인 입력(예: 존재하지 않는 계약)을 처리한다.
     *
     * @param exception 발생 예외
     * @param request   현재 HTTP 요청
     * @return 400 + RFC 7807 ProblemDetail
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleBadRequest(IllegalArgumentException exception,
                                                          HttpServletRequest request) {
        log.warn("Bad request: {}", exception.getMessage());
        int statusCode = HttpStatus.BAD_REQUEST.value();
        ProblemDetail body = ProblemDetail
                .of(statusCode, "POLICY_001")
                .title("Invalid Request")
                .detail(exception.getMessage())
                .instance(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    /**
     * 요청 바디 검증 실패를 처리한다.
     *
     * @param exception 발생 예외
     * @param request   현재 HTTP 요청
     * @return 400 + RFC 7807 ProblemDetail
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException exception,
                                                          HttpServletRequest request) {
        BindingResult bindingResult = exception.getBindingResult();
        String detail = bindingResult.getFieldErrors()
                .stream()
                .findFirst()
                .map(this::formatFieldError)
                .orElse("요청 값이 올바르지 않습니다.");
        log.warn("Validation failed: {}", detail);
        int statusCode = HttpStatus.BAD_REQUEST.value();
        ProblemDetail body = ProblemDetail
                .of(statusCode, "CLAIM_002")
                .title("Validation Failed")
                .detail(detail)
                .instance(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    /**
     * 그 외 예기치 않은 오류를 처리한다.
     *
     * @param exception 발생 예외
     * @param request   현재 HTTP 요청
     * @return 500 + RFC 7807 ProblemDetail
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception exception,
                                                       HttpServletRequest request) {
        log.error("Unhandled error", exception);
        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        ProblemDetail body = ProblemDetail
                .of(statusCode, "CLAIM_099")
                .title("Internal Server Error")
                .detail("일시적인 오류가 발생했습니다.")
                .instance(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

    /**
     * 필드 오류를 "필드명: 메시지" 형식의 문자열로 변환한다.
     *
     * @param fieldError 필드 오류
     * @return 포맷된 오류 문자열
     */
    private String formatFieldError(FieldError fieldError) {
        String fieldName = fieldError.getField();
        String message = fieldError.getDefaultMessage();
        return fieldName + ": " + message;
    }
}
