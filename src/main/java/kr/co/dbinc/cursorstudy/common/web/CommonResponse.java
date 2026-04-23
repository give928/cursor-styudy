package kr.co.dbinc.cursorstudy.common.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.time.Instant;

/**
 * API 공통 성공 응답 래퍼.
 *
 * <p>정상 응답에만 사용한다.
 * 예외 발생 시에는 {@link ProblemDetail}을 사용한다.</p>
 *
 * @param <T> {@code data} 페이로드 타입
 */
@JsonPropertyOrder({"success", "data", "timestamp"})
public class CommonResponse<T> {

    @JsonProperty("data")
    private final T data;

    @JsonUnwrapped
    private final ResponseMeta meta;

    private CommonResponse(T data) {
        this.data = data;
        this.meta = new ResponseMeta(Instant.now().toString());
    }

    /**
     * 성공 응답을 생성한다.
     *
     * @param data 본문 데이터
     * @param <T>  데이터 타입
     * @return {@link CommonResponse}
     */
    public static <T> CommonResponse<T> ok(T data) {
        return new CommonResponse<>(data);
    }

    /**
     * 응답 메타 정보 (성공 여부, 응답 시각).
     */
    static class ResponseMeta {

        @JsonProperty("success")
        private final boolean success;

        @JsonProperty("timestamp")
        private final String timestamp;

        ResponseMeta(String timestamp) {
            this.success = true;
            this.timestamp = timestamp;
        }
    }
}
