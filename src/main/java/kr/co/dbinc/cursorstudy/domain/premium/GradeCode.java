package kr.co.dbinc.cursorstudy.domain.premium;

import java.math.BigDecimal;

/**
 * 고객 등급 코드.
 * <p>VIP → GOLD → SILVER → BASIC 순서로 우선순위가 결정되며,
 * 각 등급의 적격 조건(가입 기간·무클레임·연보험료 기준)을 함께 보유한다.</p>
 */
public enum GradeCode {

    VIP(60, true, new BigDecimal("10000000")),
    GOLD(36, true, BigDecimal.ZERO),
    SILVER(12, false, BigDecimal.ZERO),
    BASIC(0, false, BigDecimal.ZERO);

    private final int minimumTenureMonths;
    private final boolean noClaimRequired;
    private final BigDecimal minimumAnnualPremium;

    GradeCode(int minimumTenureMonths, boolean noClaimRequired, BigDecimal minimumAnnualPremium) {
        this.minimumTenureMonths = minimumTenureMonths;
        this.noClaimRequired = noClaimRequired;
        this.minimumAnnualPremium = minimumAnnualPremium;
    }

    /**
     * 주어진 조건이 이 등급의 자격 요건을 충족하는지 확인한다.
     *
     * @param tenureMonths  가입 기간(개월)
     * @param noClaim       무클레임 여부
     * @param annualPremium 연 보험료
     * @return 자격 충족이면 true
     */
    public boolean isEligible(long tenureMonths, boolean noClaim, BigDecimal annualPremium) {
        if (this == BASIC) {
            return true;
        }
        if (tenureMonths < minimumTenureMonths) {
            return false;
        }
        if (noClaimRequired && !noClaim) {
            return false;
        }
        BigDecimal premium = (annualPremium != null) ? annualPremium : BigDecimal.ZERO;
        return premium.compareTo(minimumAnnualPremium) >= 0;
    }
}
