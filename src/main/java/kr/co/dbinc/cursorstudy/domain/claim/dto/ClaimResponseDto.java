package kr.co.dbinc.cursorstudy.domain.claim.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 클레임 응답 DTO.
 */
@Data
public class ClaimResponseDto {

    private Long claimSeq;
    private String claimNo;
    private Long policySeq;
    private String claimType;
    private String statusCd;
    private LocalDateTime accidentDtm;
    private LocalDateTime receiptDtm;
    private BigDecimal claimAmt;
    private BigDecimal approvedAmt;
    private BigDecimal discountRate;
    private String gradeCd;
    private String rejectReason;
}
