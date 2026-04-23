package kr.co.dbinc.cursorstudy.common.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

/**
 * API 공통 응답 래퍼.
 *
 * @param <T> 성공 시 {@code data} 페이로드 타입
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {

    private final boolean success;
    private final T data;
    private final String errorCode;
    private final String message;
    private final String timestamp;

    /**
     * 전체 필드를 지정하는 생성자.
     *
     * @param success     성공 여부
     * @param data        성공 데이터
     * @param errorCode   실패 코드
     * @param message     실패 메시지
     * @param timestamp   응답 시각(ISO-8601)
     */
    public CommonResponse(boolean success, T data, String errorCode, String message, String timestamp) {
        this.success = success;
        this.data = data;
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = timestamp;
    }

    /**
     * 성공 응답을 생성한다.
     *
     * @param data 본문 데이터
     * @param <T>  데이터 타입
     * @return {@link CommonResponse}
     */
    public static <T> CommonResponse<T> ok(T data) {
        return new CommonResponse<>(true, data, null, null, Instant.now().toString());
    }

    /**
     * 실패 응답을 생성한다.
     *
     * @param errorCode 도메인 에러 코드
     * @param message   사용자 메시지
     * @param <T>       데이터 타입(미사용)
     * @return {@link CommonResponse}
     */
    public static <T> CommonResponse<T> fail(String errorCode, String message) {
        return new CommonResponse<>(false, null, errorCode, message, Instant.now().toString());
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
