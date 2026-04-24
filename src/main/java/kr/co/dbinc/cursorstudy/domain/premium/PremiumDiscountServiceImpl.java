package kr.co.dbinc.cursorstudy.domain.premium;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.Predicate;

import kr.co.dbinc.cursorstudy.domain.premium.exception.DiscountCalculationException;
import kr.co.dbinc.cursorstudy.domain.premium.repository.CustomerRow;
import kr.co.dbinc.cursorstudy.domain.premium.repository.DiscountPolicyRow;
import kr.co.dbinc.cursorstudy.domain.premium.repository.PolicyDiscountRow;
import kr.co.dbinc.cursorstudy.domain.premium.repository.PremiumMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 할인율 계산 서비스 구현체.
 * <p>VIP → GOLD → SILVER → BASIC 순서로 조건을 확인하는 fall-through 방식을 사용한다.</p>
 */
@Slf4j
@Primary
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PremiumDiscountServiceImpl implements PremiumDiscountService {

    private final PremiumMapper premiumMapper;

    /**
     * 고객·계약 정보를 기반으로 할인율을 계산한다.
     *
     * @param customerSequence 고객 PK
     * @param policySequence   계약 PK
     * @return 할인율(예: 0.05는 5%); null 입력 시 {@link BigDecimal#ZERO}
     * @throws DiscountCalculationException 할인 정책 조회 실패 등 복구 불가능한 오류 발생 시
     */
    @Override
    public BigDecimal calculateDiscountRate(Long customerSequence, Long policySequence) {
        if (customerSequence == null || policySequence == null) {
            return BigDecimal.ZERO;
        }
        return computeRate(customerSequence, policySequence);
    }

    private BigDecimal computeRate(Long customerSequence, Long policySequence) {
        CustomerRow customer = premiumMapper.selectCustomer(customerSequence);
        if (customer == null) {
            log.warn("고객 정보 없음 — 할인율 0% 반환. custSeq={}", customerSequence);
            return BigDecimal.ZERO;
        }
        PolicyDiscountRow policy = premiumMapper.selectPolicyForDiscount(policySequence);
        if (policy == null) {
            log.warn("계약 정보 없음 — 할인율 0% 반환. policySeq={}", policySequence);
            return BigDecimal.ZERO;
        }
        return lookupDiscountRate(policySequence, policy);
    }

    private BigDecimal lookupDiscountRate(Long policySequence, PolicyDiscountRow policy) {
        int claimCount = premiumMapper.countClaimsByPolicySeq(policySequence);
        boolean noClaim = claimCount == 0;
        long tenureMonths = policy.tenureInMonths();
        BigDecimal annualPremium = policy.annualPremium();
        GradeCode grade = resolveGradeCode(tenureMonths, noClaim, annualPremium);
        log.debug("할인 등급 결정 — policySeq={}, grade={}", policySequence, grade);
        if (grade == GradeCode.BASIC) {
            return BigDecimal.ZERO;
        }
        return fetchDiscountRate(grade);
    }

    private BigDecimal fetchDiscountRate(GradeCode grade) {
        DiscountPolicyRow discountPolicy = premiumMapper.selectDiscountPolicy(grade);
        if (discountPolicy == null) {
            throw new DiscountCalculationException(
                    "할인 정책 없음 — TB_DISCOUNT_POLICY에 등급이 존재하지 않습니다. gradeCd=" + grade.name());
        }
        BigDecimal rate = discountPolicy.rate();
        log.debug("할인율 결정 — gradeCd={}, discountRate={}", grade, rate);
        return rate;
    }

    private GradeCode resolveGradeCode(long tenureMonths, boolean noClaim, BigDecimal annualPremium) {
        GradeCode[] grades = GradeCode.values();
        Predicate<GradeCode> eligible = grade -> grade.isEligible(tenureMonths, noClaim, annualPremium);
        return Arrays.stream(grades)
                .filter(eligible)
                .findFirst()
                .orElse(GradeCode.BASIC);
    }
}
