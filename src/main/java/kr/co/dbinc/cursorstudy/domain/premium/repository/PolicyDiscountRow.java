package kr.co.dbinc.cursorstudy.domain.premium.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TB_POLICY 할인율 계산용 조회 행.
 *
 * <p>{@code @Setter}/{@code @NoArgsConstructor}: MyBatis resultType 매핑을 위해 허용한다.</p>
 */
@Setter
@NoArgsConstructor
public class PolicyDiscountRow {

    private LocalDateTime contractDtm;
    private BigDecimal annualPremium;

    /**
     * 가입일 기준 현재까지의 가입 기간을 개월 수로 반환한다.
     *
     * @return 가입 기간(개월)
     */
    public long tenureInMonths() {
        LocalDateTime now = LocalDateTime.now();
        TemporalUnit monthUnit = ChronoUnit.MONTHS;
        return monthUnit.between(contractDtm, now);
    }

    /**
     * 계약의 연 보험료를 반환한다.
     *
     * @return 연 보험료
     */
    public BigDecimal annualPremium() {
        return annualPremium;
    }
}
