package kr.co.dbinc.cursorstudy.common.web;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * RFC 7807 에러 코드 값 객체.
 *
 * <p>도메인 에러 코드 문자열을 포장하고 type URI를 생성한다.</p>
 */
public class ErrorCode {

    private static final String BASE_TYPE_URI = "https://dbinc.co.kr/errors/";

    private final String code;

    private ErrorCode(String code) {
        this.code = code;
    }

    /**
     * 에러 코드 값 객체를 생성한다.
     *
     * @param code 도메인 에러 코드 (예: CLAIM_001)
     * @return {@link ErrorCode}
     */
    public static ErrorCode of(String code) {
        return new ErrorCode(code);
    }

    /**
     * 에러 유형 URI를 반환한다.
     *
     * @return type URI 문자열
     */
    public String typeUri() {
        return BASE_TYPE_URI + code;
    }

    /**
     * 에러 코드 문자열 값을 반환한다.
     *
     * @return 에러 코드
     */
    @JsonValue
    public String value() {
        return code;
    }
}
