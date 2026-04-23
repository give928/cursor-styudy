package kr.co.dbinc.cursorstudy.domain.claim.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * {@code TB_CLAIM} INSERT 파라미터.
 *
 * <p>{@code @Setter(claimSeq)}: MyBatis {@code <selectKey>}가 NEXTVAL 채번 후
 * claimSeq 필드를 직접 설정하므로 해당 필드에만 setter를 허용한다.</p>
 */
@Builder
@Getter
public class ClaimInsertParam {

    /** MyBatis selectKey가 NEXTVAL 채번 후 설정한다. */
    @Setter
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
