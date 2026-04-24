package kr.co.dbinc.cursorstudy.domain.premium;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.co.dbinc.cursorstudy.domain.premium.exception.DiscountCalculationException;
import kr.co.dbinc.cursorstudy.domain.premium.repository.CustomerRow;
import kr.co.dbinc.cursorstudy.domain.premium.repository.DiscountPolicyRow;
import kr.co.dbinc.cursorstudy.domain.premium.repository.PolicyDiscountRow;
import kr.co.dbinc.cursorstudy.domain.premium.repository.PremiumMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PremiumDiscountServiceImpl.calculateDiscountRate() 단위 테스트.
 *
 * <p>PremiumMapper는 Mockito Mock으로 대체하며, 각 테스트는 독립적으로 실행 가능하다.</p>
 */
@ExtendWith(MockitoExtension.class)
class PremiumDiscountServiceTest {

    @Mock
    private PremiumMapper premiumMapper;

    @InjectMocks
    private PremiumDiscountServiceImpl premiumDiscountService;

    private static final Long CUSTOMER_SEQ = 1L;
    private static final Long POLICY_SEQ = 100L;

    // ──────────────────────────────────────────────
    // 테스트 픽스처 헬퍼
    // ──────────────────────────────────────────────

    private CustomerRow customerRow() {
        return new CustomerRow();
    }

    private PolicyDiscountRow policyRow(long monthsAgo, BigDecimal annualPremium) {
        PolicyDiscountRow row = new PolicyDiscountRow();
        row.setContractDtm(LocalDateTime.now().minusMonths(monthsAgo));
        row.setAnnualPremium(annualPremium);
        return row;
    }

    private DiscountPolicyRow discountPolicyRow(String rate) {
        DiscountPolicyRow row = new DiscountPolicyRow();
        row.setDiscountRate(new BigDecimal(rate));
        return row;
    }

    // ──────────────────────────────────────────────
    // 1. 정상 케이스
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("정상 케이스")
    class NormalCases {

        @Test
        @DisplayName("VIP 조건(60개월·무클레임·연 1500만원) 모두 충족 시 10% 반환")
        void calculateDiscountRate_VIPAllConditionsMet_Returns10Percent() {
            // given
            when(premiumMapper.selectCustomer(CUSTOMER_SEQ)).thenReturn(customerRow());
            when(premiumMapper.selectPolicyForDiscount(POLICY_SEQ))
                    .thenReturn(policyRow(72, new BigDecimal("15000000")));
            when(premiumMapper.countClaimsByPolicySeq(POLICY_SEQ)).thenReturn(0);
            when(premiumMapper.selectDiscountPolicy(GradeCode.VIP))
                    .thenReturn(discountPolicyRow("0.10"));

            // when
            BigDecimal result = premiumDiscountService.calculateDiscountRate(CUSTOMER_SEQ, POLICY_SEQ);

            // then
            assertThat(result.compareTo(new BigDecimal("0.10"))).isZero();
        }

        @Test
        @DisplayName("GOLD 조건(36개월·무클레임·연보험료 1000만원 미만) 충족 시 5% 반환")
        void calculateDiscountRate_GoldConditionsMet_Returns5Percent() {
            // given
            when(premiumMapper.selectCustomer(CUSTOMER_SEQ)).thenReturn(customerRow());
            when(premiumMapper.selectPolicyForDiscount(POLICY_SEQ))
                    .thenReturn(policyRow(48, new BigDecimal("5000000")));
            when(premiumMapper.countClaimsByPolicySeq(POLICY_SEQ)).thenReturn(0);
            when(premiumMapper.selectDiscountPolicy(GradeCode.GOLD))
                    .thenReturn(discountPolicyRow("0.05"));

            // when
            BigDecimal result = premiumDiscountService.calculateDiscountRate(CUSTOMER_SEQ, POLICY_SEQ);

            // then
            assertThat(result.compareTo(new BigDecimal("0.05"))).isZero();
        }

        @Test
        @DisplayName("SILVER 조건(12개월 이상·클레임 존재) 충족 시 3% 반환")
        void calculateDiscountRate_SilverConditionsMet_Returns3Percent() {
            // given
            when(premiumMapper.selectCustomer(CUSTOMER_SEQ)).thenReturn(customerRow());
            when(premiumMapper.selectPolicyForDiscount(POLICY_SEQ))
                    .thenReturn(policyRow(24, new BigDecimal("3000000")));
            when(premiumMapper.countClaimsByPolicySeq(POLICY_SEQ)).thenReturn(1);
            when(premiumMapper.selectDiscountPolicy(GradeCode.SILVER))
                    .thenReturn(discountPolicyRow("0.03"));

            // when
            BigDecimal result = premiumDiscountService.calculateDiscountRate(CUSTOMER_SEQ, POLICY_SEQ);

            // then
            assertThat(result.compareTo(new BigDecimal("0.03"))).isZero();
        }

        @Test
        @DisplayName("가입 12개월 미만으로 모든 조건 불충족 시 BASIC 0% 반환")
        void calculateDiscountRate_NoConditionMet_ReturnsZero() {
            // given
            when(premiumMapper.selectCustomer(CUSTOMER_SEQ)).thenReturn(customerRow());
            when(premiumMapper.selectPolicyForDiscount(POLICY_SEQ))
                    .thenReturn(policyRow(11, new BigDecimal("20000000")));
            when(premiumMapper.countClaimsByPolicySeq(POLICY_SEQ)).thenReturn(0);

            // when
            BigDecimal result = premiumDiscountService.calculateDiscountRate(CUSTOMER_SEQ, POLICY_SEQ);

            // then
            assertThat(result.compareTo(BigDecimal.ZERO)).isZero();
        }
    }

    // ──────────────────────────────────────────────
    // 2. 등급 순차 적용 (Fall-through)
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("등급 순차 적용(Fall-through)")
    class FallThroughCases {

        @Test
        @DisplayName("VIP 연보험료 1원 미달 시 GOLD로 fall-through하여 5% 반환")
        void calculateDiscountRate_VIPPremiumNotMet_FallsToGoldReturns5Percent() {
            // given
            when(premiumMapper.selectCustomer(CUSTOMER_SEQ)).thenReturn(customerRow());
            when(premiumMapper.selectPolicyForDiscount(POLICY_SEQ))
                    .thenReturn(policyRow(60, new BigDecimal("9999999")));
            when(premiumMapper.countClaimsByPolicySeq(POLICY_SEQ)).thenReturn(0);
            when(premiumMapper.selectDiscountPolicy(GradeCode.GOLD))
                    .thenReturn(discountPolicyRow("0.05"));

            // when
            BigDecimal result = premiumDiscountService.calculateDiscountRate(CUSTOMER_SEQ, POLICY_SEQ);

            // then
            assertThat(result.compareTo(new BigDecimal("0.05"))).isZero();
        }

        @Test
        @DisplayName("VIP 기간·보험료 충족 중 클레임 존재 시 VIP·GOLD 모두 탈락, SILVER로 fall-through하여 3% 반환")
        void calculateDiscountRate_VIPClaimExists_FallsToSilverReturns3Percent() {
            // given
            // GOLD도 noClaimRequired=true이므로 클레임 존재 시 VIP·GOLD 모두 탈락한다.
            when(premiumMapper.selectCustomer(CUSTOMER_SEQ)).thenReturn(customerRow());
            when(premiumMapper.selectPolicyForDiscount(POLICY_SEQ))
                    .thenReturn(policyRow(72, new BigDecimal("15000000")));
            when(premiumMapper.countClaimsByPolicySeq(POLICY_SEQ)).thenReturn(1);
            when(premiumMapper.selectDiscountPolicy(GradeCode.SILVER))
                    .thenReturn(discountPolicyRow("0.03"));

            // when
            BigDecimal result = premiumDiscountService.calculateDiscountRate(CUSTOMER_SEQ, POLICY_SEQ);

            // then
            assertThat(result.compareTo(new BigDecimal("0.03"))).isZero();
        }

        @Test
        @DisplayName("GOLD 조건 충족 중 클레임 존재 시 SILVER로 fall-through하여 3% 반환")
        void calculateDiscountRate_GoldClaimExists_FallsToSilverReturns3Percent() {
            // given
            when(premiumMapper.selectCustomer(CUSTOMER_SEQ)).thenReturn(customerRow());
            when(premiumMapper.selectPolicyForDiscount(POLICY_SEQ))
                    .thenReturn(policyRow(48, new BigDecimal("5000000")));
            when(premiumMapper.countClaimsByPolicySeq(POLICY_SEQ)).thenReturn(2);
            when(premiumMapper.selectDiscountPolicy(GradeCode.SILVER))
                    .thenReturn(discountPolicyRow("0.03"));

            // when
            BigDecimal result = premiumDiscountService.calculateDiscountRate(CUSTOMER_SEQ, POLICY_SEQ);

            // then
            assertThat(result.compareTo(new BigDecimal("0.03"))).isZero();
        }

        @Test
        @DisplayName("GOLD 가입 기간(35개월) 미달 시 SILVER로 fall-through하여 3% 반환")
        void calculateDiscountRate_GoldTenureNotMet_FallsToSilverReturns3Percent() {
            // given
            when(premiumMapper.selectCustomer(CUSTOMER_SEQ)).thenReturn(customerRow());
            when(premiumMapper.selectPolicyForDiscount(POLICY_SEQ))
                    .thenReturn(policyRow(35, new BigDecimal("5000000")));
            when(premiumMapper.countClaimsByPolicySeq(POLICY_SEQ)).thenReturn(0);
            when(premiumMapper.selectDiscountPolicy(GradeCode.SILVER))
                    .thenReturn(discountPolicyRow("0.03"));

            // when
            BigDecimal result = premiumDiscountService.calculateDiscountRate(CUSTOMER_SEQ, POLICY_SEQ);

            // then
            assertThat(result.compareTo(new BigDecimal("0.03"))).isZero();
        }
    }

    // ──────────────────────────────────────────────
    // 3. 경계값
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("경계값")
    class BoundaryCases {

        @Test
        @DisplayName("가입 정확히 60개월째 되는 날 VIP 자격 충족 → 10% 반환")
        void calculateDiscountRate_TenureExactly60Months_QualifiesVipReturns10Percent() {
            // given
            when(premiumMapper.selectCustomer(CUSTOMER_SEQ)).thenReturn(customerRow());
            when(premiumMapper.selectPolicyForDiscount(POLICY_SEQ))
                    .thenReturn(policyRow(60, new BigDecimal("10000000")));
            when(premiumMapper.countClaimsByPolicySeq(POLICY_SEQ)).thenReturn(0);
            when(premiumMapper.selectDiscountPolicy(GradeCode.VIP))
                    .thenReturn(discountPolicyRow("0.10"));

            // when
            BigDecimal result = premiumDiscountService.calculateDiscountRate(CUSTOMER_SEQ, POLICY_SEQ);

            // then
            assertThat(result.compareTo(new BigDecimal("0.10"))).isZero();
        }

        @Test
        @DisplayName("가입 59개월(VIP 기간 1개월 미달) 시 GOLD로 fall-through → 5% 반환")
        void calculateDiscountRate_TenureExactly59Months_DoesNotQualifyVipFallsToGold() {
            // given
            when(premiumMapper.selectCustomer(CUSTOMER_SEQ)).thenReturn(customerRow());
            when(premiumMapper.selectPolicyForDiscount(POLICY_SEQ))
                    .thenReturn(policyRow(59, new BigDecimal("10000000")));
            when(premiumMapper.countClaimsByPolicySeq(POLICY_SEQ)).thenReturn(0);
            when(premiumMapper.selectDiscountPolicy(GradeCode.GOLD))
                    .thenReturn(discountPolicyRow("0.05"));

            // when
            BigDecimal result = premiumDiscountService.calculateDiscountRate(CUSTOMER_SEQ, POLICY_SEQ);

            // then
            assertThat(result.compareTo(new BigDecimal("0.05"))).isZero();
        }

        @Test
        @DisplayName("연보험료 정확히 1,000만원 시 VIP 자격 충족 → 10% 반환")
        void calculateDiscountRate_AnnualPremiumExactly10Million_QualifiesVipReturns10Percent() {
            // given
            when(premiumMapper.selectCustomer(CUSTOMER_SEQ)).thenReturn(customerRow());
            when(premiumMapper.selectPolicyForDiscount(POLICY_SEQ))
                    .thenReturn(policyRow(72, new BigDecimal("10000000")));
            when(premiumMapper.countClaimsByPolicySeq(POLICY_SEQ)).thenReturn(0);
            when(premiumMapper.selectDiscountPolicy(GradeCode.VIP))
                    .thenReturn(discountPolicyRow("0.10"));

            // when
            BigDecimal result = premiumDiscountService.calculateDiscountRate(CUSTOMER_SEQ, POLICY_SEQ);

            // then
            assertThat(result.compareTo(new BigDecimal("0.10"))).isZero();
        }

        @Test
        @DisplayName("연보험료 9,999,999원(1원 미달) 시 VIP 자격 미달 → GOLD fall-through 5% 반환")
        void calculateDiscountRate_AnnualPremiumOneBelowThreshold_DoesNotQualifyVipFallsToGold() {
            // given
            when(premiumMapper.selectCustomer(CUSTOMER_SEQ)).thenReturn(customerRow());
            when(premiumMapper.selectPolicyForDiscount(POLICY_SEQ))
                    .thenReturn(policyRow(72, new BigDecimal("9999999")));
            when(premiumMapper.countClaimsByPolicySeq(POLICY_SEQ)).thenReturn(0);
            when(premiumMapper.selectDiscountPolicy(GradeCode.GOLD))
                    .thenReturn(discountPolicyRow("0.05"));

            // when
            BigDecimal result = premiumDiscountService.calculateDiscountRate(CUSTOMER_SEQ, POLICY_SEQ);

            // then
            assertThat(result.compareTo(new BigDecimal("0.05"))).isZero();
        }
    }

    // ──────────────────────────────────────────────
    // 4. 예외·방어 케이스
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("예외·방어 케이스")
    class DefensiveCases {

        @Test
        @DisplayName("customerSequence가 null이면 Mapper 호출 없이 BigDecimal.ZERO 반환")
        void calculateDiscountRate_CustomerSequenceIsNull_ReturnsZero() {
            // given
            Long customerSequence = null;

            // when
            BigDecimal result = premiumDiscountService.calculateDiscountRate(customerSequence, POLICY_SEQ);

            // then
            assertThat(result.compareTo(BigDecimal.ZERO)).isZero();
        }

        @Test
        @DisplayName("policySequence가 null이면 Mapper 호출 없이 BigDecimal.ZERO 반환")
        void calculateDiscountRate_PolicySequenceIsNull_ReturnsZero() {
            // given
            Long policySequence = null;

            // when
            BigDecimal result = premiumDiscountService.calculateDiscountRate(CUSTOMER_SEQ, policySequence);

            // then
            assertThat(result.compareTo(BigDecimal.ZERO)).isZero();
        }

        @Test
        @DisplayName("두 파라미터 모두 null이면 Mapper 호출 없이 BigDecimal.ZERO 반환")
        void calculateDiscountRate_BothParametersNull_ReturnsZero() {
            // given
            Long customerSequence = null;
            Long policySequence = null;

            // when
            BigDecimal result = premiumDiscountService.calculateDiscountRate(customerSequence, policySequence);

            // then
            assertThat(result.compareTo(BigDecimal.ZERO)).isZero();
        }

        @Test
        @DisplayName("고객 정보가 DB에 없으면 BigDecimal.ZERO 반환")
        void calculateDiscountRate_CustomerNotFoundInDb_ReturnsZero() {
            // given
            when(premiumMapper.selectCustomer(CUSTOMER_SEQ)).thenReturn(null);

            // when
            BigDecimal result = premiumDiscountService.calculateDiscountRate(CUSTOMER_SEQ, POLICY_SEQ);

            // then
            assertThat(result.compareTo(BigDecimal.ZERO)).isZero();
        }

        @Test
        @DisplayName("계약 정보가 DB에 없으면 BigDecimal.ZERO 반환")
        void calculateDiscountRate_PolicyNotFoundInDb_ReturnsZero() {
            // given
            when(premiumMapper.selectCustomer(CUSTOMER_SEQ)).thenReturn(customerRow());
            when(premiumMapper.selectPolicyForDiscount(POLICY_SEQ)).thenReturn(null);

            // when
            BigDecimal result = premiumDiscountService.calculateDiscountRate(CUSTOMER_SEQ, POLICY_SEQ);

            // then
            assertThat(result.compareTo(BigDecimal.ZERO)).isZero();
        }

        @Test
        @DisplayName("TB_DISCOUNT_POLICY에 해당 등급이 없으면 DiscountCalculationException 발생")
        void calculateDiscountRate_DiscountPolicyNotFoundForGrade_ThrowsDiscountCalculationException() {
            // given
            when(premiumMapper.selectCustomer(CUSTOMER_SEQ)).thenReturn(customerRow());
            when(premiumMapper.selectPolicyForDiscount(POLICY_SEQ))
                    .thenReturn(policyRow(72, new BigDecimal("15000000")));
            when(premiumMapper.countClaimsByPolicySeq(POLICY_SEQ)).thenReturn(0);
            when(premiumMapper.selectDiscountPolicy(GradeCode.VIP)).thenReturn(null);

            // when & then
            assertThatThrownBy(
                    () -> premiumDiscountService.calculateDiscountRate(CUSTOMER_SEQ, POLICY_SEQ))
                    .isInstanceOf(DiscountCalculationException.class)
                    .hasMessageContaining("TB_DISCOUNT_POLICY");
        }
    }
}
