package kr.co.dbinc.cursorstudy.domain.claim.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

/**
 * 클레임 접수 요청 DTO.
 */
@Data
public class ClaimRequestDto {

    @NotNull
    @Positive
    private Long policySeq;

    @NotBlank
    private String claimType;

    @NotNull
    private LocalDateTime accidentDtm;

    @NotNull
    @Positive
    private BigDecimal claimAmt;

    /**
     * 등록/수정자 ID(미입력 시 서비스에서 SYSTEM으로 대체).
     */
    private String actorId;
}
