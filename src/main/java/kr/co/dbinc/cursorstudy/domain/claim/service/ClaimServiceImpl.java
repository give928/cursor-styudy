package kr.co.dbinc.cursorstudy.domain.claim.service;

import kr.co.dbinc.cursorstudy.domain.claim.dto.ClaimRequestDto;
import kr.co.dbinc.cursorstudy.domain.claim.dto.ClaimResponseDto;
import kr.co.dbinc.cursorstudy.domain.claim.exception.ClaimNotFoundException;
import kr.co.dbinc.cursorstudy.domain.claim.repository.AuditLogInsertParam;
import kr.co.dbinc.cursorstudy.domain.claim.repository.ClaimInsertParam;
import kr.co.dbinc.cursorstudy.domain.claim.repository.ClaimMapper;
import kr.co.dbinc.cursorstudy.domain.claim.repository.PolicyWithGradeRow;
import kr.co.dbinc.cursorstudy.domain.premium.PremiumDiscountService;

import java.math.BigDecimal;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 클레임 도메인 서비스 구현.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClaimServiceImpl implements ClaimService {

    private static final String DEFAULT_ACTOR = "SYSTEM";

    private final ClaimMapper claimMapper;
    private final PremiumDiscountService premiumDiscountService;

    /**
     * 클레임을 접수하고 감사 로그를 남긴다.
     *
     * @param request 접수 요청
     * @return 저장된 클레임 정보
     */
    @Override
    @Transactional
    public ClaimResponseDto registerClaim(ClaimRequestDto request) {
        PolicyWithGradeRow policyRow = fetchPolicyOrThrow(request.getPolicySeq());

        Long customerSequence = policyRow.getCustSeq();
        Long policySequence = policyRow.getPolicySeq();
        BigDecimal discountRate = premiumDiscountService.calculateDiscountRate(customerSequence, policySequence);

        ClaimInsertParam insertParam = buildInsertParam(request, policyRow, discountRate);
        claimMapper.insertClaim(insertParam);
        claimMapper.insertAuditLog(buildAuditLog(insertParam, request.getActorId()));

        return fetchSavedClaimOrThrow(insertParam.getClaimSeq());
    }

    /**
     * 클레임 단건을 조회한다.
     *
     * @param claimSeq 클레임 PK
     * @return 클레임 정보
     */
    @Override
    public ClaimResponseDto getClaim(Long claimSeq) {
        ClaimResponseDto row = claimMapper.selectClaimByClaimSeq(claimSeq);
        if (row == null) {
            throw new ClaimNotFoundException(claimSeq);
        }
        return row;
    }

    // ── private helpers ────────────────────────────────────────────────────

    /**
     * 계약-고객 등급 행을 조회하고, 없으면 예외를 던진다.
     */
    private PolicyWithGradeRow fetchPolicyOrThrow(Long policySeq) {
        PolicyWithGradeRow row = claimMapper.selectPolicyWithGrade(policySeq);
        if (row == null) {
            throw new IllegalArgumentException("존재하지 않는 계약입니다. policySeq=" + policySeq);
        }
        return row;
    }

    /**
     * 요청·정책 행·할인율로 INSERT 파라미터를 조립한다.
     */
    private ClaimInsertParam buildInsertParam(ClaimRequestDto request,
                                              PolicyWithGradeRow policyRow,
                                              BigDecimal discountRate) {
        String actorId = request.getActorId();
        String actor = Optional.ofNullable(actorId).orElse(DEFAULT_ACTOR);
        String gradeCd = policyRow.getGradeCd();
        return ClaimInsertParam.builder()
                .policySeq(request.getPolicySeq())
                .claimType(request.getClaimType())
                .statusCd("RECEIPT")
                .accidentDtm(request.getAccidentDtm())
                .claimAmt(request.getClaimAmt())
                .discountRate(discountRate)
                .gradeCd(gradeCd)
                .regId(actor)
                .updId(actor)
                .build();
    }

    /**
     * INSERT 파라미터로 감사 로그 파라미터를 조립한다.
     */
    private AuditLogInsertParam buildAuditLog(ClaimInsertParam insertParam, String actorId) {
        String actor = Optional.ofNullable(actorId).orElse(DEFAULT_ACTOR);
        return AuditLogInsertParam.builder()
                .actionCd("INSERT")
                .targetTable("TB_CLAIM")
                .targetSeq(insertParam.getClaimSeq())
                .beforeValue(null)
                .afterValue(buildAfterAuditPayload(insertParam))
                .userId(actor)
                .clientIp(null)
                .regId(actor)
                .updId(actor)
                .build();
    }

    /**
     * INSERT 직후 저장된 클레임을 조회하고, 없으면 예외를 던진다.
     */
    private ClaimResponseDto fetchSavedClaimOrThrow(Long claimSeq) {
        ClaimResponseDto saved = claimMapper.selectClaimByClaimSeq(claimSeq);
        if (saved == null) {
            throw new IllegalStateException("클레임 저장 후 조회 실패. claimSeq=" + claimSeq);
        }
        // 등급·할인율은 민감 정보이므로 DEBUG 레벨로만 기록한다
        Long savedClaimSeq = saved.getClaimSeq();
        Long savedPolicySeq = saved.getPolicySeq();
        String savedGradeCd = saved.getGradeCd();
        BigDecimal savedDiscountRate = saved.getDiscountRate();
        log.debug("Claim registered claimSeq={}, policySeq={}, gradeCd={}, discountRate={}",
                savedClaimSeq, savedPolicySeq, savedGradeCd, savedDiscountRate);
        return saved;
    }

    private String buildAfterAuditPayload(ClaimInsertParam insertParam) {
        Long claimSeq = insertParam.getClaimSeq();
        Long policySeq = insertParam.getPolicySeq();
        String statusCd = Optional.ofNullable(insertParam.getStatusCd()).orElse("RECEIPT");
        String gradeCd = Optional.ofNullable(insertParam.getGradeCd()).orElse("");
        BigDecimal discountRate = insertParam.getDiscountRate();
        String discountRateStr = discountRate == null ? "0" : discountRate.toPlainString();
        return String.format(
                "{\"claimSeq\":%d,\"policySeq\":%d,\"statusCd\":\"%s\",\"gradeCd\":\"%s\",\"discountRate\":%s}",
                claimSeq, policySeq, statusCd, gradeCd, discountRateStr);
    }
}
