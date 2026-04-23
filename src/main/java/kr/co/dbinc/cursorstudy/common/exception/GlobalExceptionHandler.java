package kr.co.dbinc.cursorstudy.common.exception;

import kr.co.dbinc.cursorstudy.common.web.CommonResponse;
import kr.co.dbinc.cursorstudy.domain.claim.exception.ClaimNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * REST API 전역 예외 처리.
 */
@Slf4j
@RestControllerAdvice(basePackages = "kr.co.dbinc.cursorstudy")
public class GlobalExceptionHandler {

    /**
     * 존재하지 않는 클레임 조회 요청을 처리한다.
     *
     * @param ex 발생 예외
     * @return 404 + 공통 실패 응답
     */
    @ExceptionHandler(ClaimNotFoundException.class)
    public ResponseEntity<CommonResponse<Void>> handleClaimNotFound(ClaimNotFoundException ex) {
        log.warn("Claim not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.fail("CLAIM_001", ex.getMessage()));
    }

    /**
     * 잘못된 도메인 입력(예: 존재하지 않는 계약)을 처리한다.
     *
     * @param ex 발생 예외
     * @return 400 + 공통 실패 응답
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<Void>> handleBadRequest(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.fail("POLICY_001", ex.getMessage()));
    }

    /**
     * 요청 바디 검증 실패를 처리한다.
     *
     * @param ex 발생 예외
     * @return 400 + 공통 실패 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("요청 값이 올바르지 않습니다.");
        log.warn("Validation failed: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.fail("CLAIM_002", message));
    }

    /**
     * 그 외 예기치 않은 오류를 처리한다.
     *
     * @param ex 발생 예외
     * @return 500 + 공통 실패 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleGeneric(Exception ex) {
        log.error("Unhandled error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.fail("CLAIM_099", "일시적인 오류가 발생했습니다."));
    }
}
