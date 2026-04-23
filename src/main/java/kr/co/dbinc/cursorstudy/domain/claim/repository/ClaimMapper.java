package kr.co.dbinc.cursorstudy.domain.claim.repository;

import kr.co.dbinc.cursorstudy.domain.claim.dto.ClaimResponseDto;
import org.apache.ibatis.annotations.Param;

/**
 * {@code TB_CLAIM}, {@code TB_AUDIT_LOG} 등 클레임 관련 MyBatis 매퍼.
 */
public interface ClaimMapper {

    /**
     * 계약에 연결된 고객 등급을 조회한다.
     *
     * @param policySeq 계약 PK
     * @return 정책·고객 등급 행(없으면 null)
     */
    PolicyWithGradeRow selectPolicyWithGrade(@Param("policySeq") Long policySeq);

    /**
     * 클레임을 저장한다.
     *
     * @param param INSERT 파라미터({@code claimSeq}는 selectKey로 채워짐)
     */
    void insertClaim(ClaimInsertParam param);

    /**
     * 감사 로그를 저장한다.
     *
     * @param param INSERT 파라미터
     */
    void insertAuditLog(AuditLogInsertParam param);

    /**
     * 클레임 단건을 조회한다.
     *
     * @param claimSeq 클레임 PK
     * @return 클레임 행(없으면 null)
     */
    ClaimResponseDto selectClaimByClaimSeq(@Param("claimSeq") Long claimSeq);
}
