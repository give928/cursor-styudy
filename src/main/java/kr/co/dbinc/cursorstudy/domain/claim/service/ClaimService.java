package kr.co.dbinc.cursorstudy.domain.claim.service;

import kr.co.dbinc.cursorstudy.domain.claim.dto.ClaimRequestDto;
import kr.co.dbinc.cursorstudy.domain.claim.dto.ClaimResponseDto;

/**
 * 클레임 도메인 서비스.
 */
public interface ClaimService {

    /**
     * 클레임을 접수한다.
     *
     * @param request 접수 요청
     * @return 저장된 클레임 정보
     */
    ClaimResponseDto registerClaim(ClaimRequestDto request);

    /**
     * 클레임 단건을 조회한다.
     *
     * @param claimSeq 클레임 PK
     * @return 클레임 정보
     */
    ClaimResponseDto getClaim(Long claimSeq);
}
