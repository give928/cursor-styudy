package kr.co.dbinc.cursorstudy.domain.claim.repository;

import lombok.Data;

/**
 * {@code TB_AUDIT_LOG} INSERT 파라미터.
 */
@Data
public class AuditLogInsertParam {

    private String actionCd;
    private String targetTable;
    private Long targetSeq;
    private String beforeValue;
    private String afterValue;
    private String userId;
    private String clientIp;
    private String regId;
    private String updId;
}
