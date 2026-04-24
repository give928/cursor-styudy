package kr.co.dbinc.cursorstudy.domain.premium.repository;

import kr.co.dbinc.cursorstudy.domain.premium.GradeCode;
import org.apache.ibatis.annotations.Param;

/**
 * 할인율 계산에 필요한 TB_CUSTOMER / TB_POLICY / TB_CLAIM / TB_DISCOUNT_POLICY 조회 매퍼.
 */
public interface PremiumMapper {

    /**
     * 고객 존재 여부를 확인한다. null 반환 시 해당 고객이 없음을 의미한다.
     *
     * @param customerSequence 고객 PK
     * @return 고객 행(없으면 null)
     */
    CustomerRow selectCustomer(@Param("custSeq") Long customerSequence);

    /**
     * 계약의 가입일·연 보험료를 조회한다.
     *
     * @param policySequence 계약 PK
     * @return 계약 할인 계산용 행(없으면 null)
     */
    PolicyDiscountRow selectPolicyForDiscount(@Param("policySeq") Long policySequence);

    /**
     * 특정 계약의 클레임 건수를 조회한다.
     *
     * @param policySequence 계약 PK
     * @return 클레임 건수(0이면 무클레임)
     */
    int countClaimsByPolicySeq(@Param("policySeq") Long policySequence);

    /**
     * 등급에 해당하는 할인 정책을 조회한다.
     *
     * @param gradeCode 등급 코드
     * @return 할인 정책 행(없으면 null)
     */
    DiscountPolicyRow selectDiscountPolicy(@Param("gradeCd") GradeCode gradeCode);
}
