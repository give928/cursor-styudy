package kr.co.dbinc.cursorstudy.domain.claim.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * {@code TB_CLAIM} INSERT 파라미터.
 */
@Data
public class ClaimInsertParam {

    private Long claimSeq;
    private Long policySeq;
    private String claimType;
    private String statusCd;
    private LocalDateTime accidentDtm;
    private BigDecimal claimAmt;
    private BigDecimal discountRate;
    private String gradeCd;
    private String regId;
    private String updId;
}
