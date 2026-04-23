package kr.co.dbinc.cursorstudy.domain.premium;

import java.math.BigDecimal;

/**
 * 보험료 할인율 계산 포트.
 * <p>실제 정책(fall-through, TB_DISCOUNT_POLICY 연동 등)은 후속 세션에서 구현한다.</p>
 */
public interface PremiumDiscountService {

    /**
     * 고객 등급 코드를 기준으로 할인율을 계산한다.
     *
     * @param gradeCd {@code TB_CUSTOMER.GRADE_CD}
     * @return 할인율(예: 0.05는 5%)
     */
    BigDecimal calculateDiscountRate(String gradeCd);
}
