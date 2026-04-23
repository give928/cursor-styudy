package kr.co.dbinc.cursorstudy.common.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.time.Instant;

/**
 * RFC 7807 Problem Details for HTTP APIs 규격 응답 객체.
 *
 * <p>예외 발생 시 {@link kr.co.dbinc.cursorstudy.common.exception.GlobalExceptionHandler}가
 * 이 객체를 반환한다. 성공 응답에는 사용하지 않는다.</p>
 *
 * <p>Spring Boot 3(Spring Framework 6) 으로 업그레이드 시
 * {@code org.springframework.http.ProblemDetail}로 대체할 것.</p>
 *
 * <pre>RFC 7807 표준 필드:
 *   type     — 문제 유형을 식별하는 URI
 *   title    — 문제 유형의 짧은 요약
 *   status   — HTTP 상태 코드
 *   detail   — 이번 발생에 특화된 사람이 읽을 수 있는 설명
 *   instance — 이번 발생을 식별하는 URI (요청 경로)
 * 확장 필드:
 *   errorCode — 도메인 에러 코드 (예: CLAIM_001)
 *   timestamp — 발생 시각 (ISO-8601)
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDetail {

    @JsonUnwrapped
    private final ProblemIdentifier identifier;

    @JsonUnwrapped
    private final ProblemBody body;

    private ProblemDetail(ProblemIdentifier identifier, ProblemBody body) {
        this.identifier = identifier;
        this.body = body;
    }

    /**
     * {@link ProblemDetail} 빌더를 반환한다.
     *
     * @param status    HTTP 상태 코드
     * @param errorCode 도메인 에러 코드 (예: CLAIM_001)
     * @return {@link Builder}
     */
    public static Builder of(int status, String errorCode) {
        return new Builder(status, errorCode);
    }

    /**
     * {@link ProblemDetail} 빌더.
     */
    public static class Builder {

        private final int status;
        private final ErrorCode errorCode;
        private String title;
        private String detail;
        private String instance;

        private Builder(int status, String errorCode) {
            this.status = status;
            this.errorCode = ErrorCode.of(errorCode);
        }

        /**
         * 문제 유형의 짧은 요약을 설정한다.
         *
         * @param title 요약
         * @return {@link Builder}
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * 발생별 상세 설명을 설정한다.
         *
         * @param detail 설명
         * @return {@link Builder}
         */
        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        /**
         * 요청 경로(instance URI)를 설정한다.
         *
         * @param instance 요청 경로
         * @return {@link Builder}
         */
        public Builder instance(String instance) {
            this.instance = instance;
            return this;
        }

        /**
         * {@link ProblemDetail}을 생성한다.
         *
         * @return {@link ProblemDetail}
         */
        public ProblemDetail build() {
            ProblemIdentifier problemIdentifier = new ProblemIdentifier(errorCode);
            Instant now = Instant.now();
            String timestamp = now.toString();
            ProblemLocation location = new ProblemLocation(instance, timestamp);
            ProblemDescription description = new ProblemDescription(detail, location);
            ProblemStatus statusHeader = new ProblemStatus(status, title);
            ProblemBody problemBody = new ProblemBody(statusHeader, description);
            return new ProblemDetail(problemIdentifier, problemBody);
        }
    }

    private static class ProblemIdentifier {

        @JsonProperty("type")
        private final String typeUri;

        @JsonProperty("errorCode")
        private final ErrorCode errorCode;

        private ProblemIdentifier(ErrorCode errorCode) {
            this.typeUri = errorCode.typeUri();
            this.errorCode = errorCode;
        }
    }

    private static class ProblemBody {

        @JsonUnwrapped
        private final ProblemStatus statusHeader;

        @JsonUnwrapped
        private final ProblemDescription description;

        private ProblemBody(ProblemStatus statusHeader, ProblemDescription description) {
            this.statusHeader = statusHeader;
            this.description = description;
        }
    }

    private static class ProblemStatus {

        @JsonProperty("status")
        private final int status;

        @JsonProperty("title")
        private final String title;

        private ProblemStatus(int status, String title) {
            this.status = status;
            this.title = title;
        }
    }

    private static class ProblemDescription {

        @JsonProperty("detail")
        private final String detail;

        @JsonUnwrapped
        private final ProblemLocation location;

        private ProblemDescription(String detail, ProblemLocation location) {
            this.detail = detail;
            this.location = location;
        }
    }

    private static class ProblemLocation {

        @JsonProperty("instance")
        private final String instance;

        @JsonProperty("timestamp")
        private final String timestamp;

        private ProblemLocation(String instance, String timestamp) {
            this.instance = instance;
            this.timestamp = timestamp;
        }
    }
}
