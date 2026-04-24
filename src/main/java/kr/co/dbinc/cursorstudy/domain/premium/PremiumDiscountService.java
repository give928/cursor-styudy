package kr.co.dbinc.cursorstudy.domain.premium;

import java.math.BigDecimal;

/**
 * 보험료 할인율 계산 포트.
 * <p>VIP → GOLD → SILVER → BASIC 순서로 fall-through 방식으로 할인율을 결정한다.</p>
 */
public interface PremiumDiscountService {

    /**
     * 고객·계약 정보를 기반으로 할인율을 계산한다.
     *
     * @param customerSequence 고객 PK ({@code TB_CUSTOMER.CUST_SEQ})
     * @param policySequence   계약 PK ({@code TB_POLICY.POLICY_SEQ})
     * @return 할인율(예: 0.05는 5%); null 입력 시 {@link java.math.BigDecimal#ZERO}
     */
    BigDecimal calculateDiscountRate(Long customerSequence, Long policySequence);
}
