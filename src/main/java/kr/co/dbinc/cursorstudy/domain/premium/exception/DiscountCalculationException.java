package kr.co.dbinc.cursorstudy.domain.premium.exception;

/**
 * 할인율 계산 중 복구 불가능한 오류 발생 시 던지는 예외.
 */
public class DiscountCalculationException extends RuntimeException {

    /**
     * 오류 메시지를 지정해 예외를 생성한다.
     *
     * @param message 오류 상세 메시지
     */
    public DiscountCalculationException(String message) {
        super(message);
    }

    /**
     * 오류 메시지와 원인 예외를 지정해 예외를 생성한다.
     *
     * @param message 오류 상세 메시지
     * @param cause   원인 예외
     */
    public DiscountCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
