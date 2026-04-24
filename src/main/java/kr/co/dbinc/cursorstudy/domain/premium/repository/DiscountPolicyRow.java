package kr.co.dbinc.cursorstudy.domain.premium.repository;

import java.math.BigDecimal;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TB_DISCOUNT_POLICY 조회 행.
 *
 * <p>{@code @Setter}/{@code @NoArgsConstructor}: MyBatis resultType 매핑을 위해 허용한다.</p>
 */
@Setter
@NoArgsConstructor
public class DiscountPolicyRow {

    private BigDecimal discountRate;

    /**
     * 이 등급에 적용되는 할인율을 반환한다.
     *
     * @return 할인율(예: 0.05는 5%)
     */
    public BigDecimal rate() {
        return discountRate;
    }
}
