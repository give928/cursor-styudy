package kr.co.dbinc.cursorstudy.domain.claim.repository;

import lombok.Data;

/**
 * 계약-고객 등급 조회용 매퍼 행.
 */
@Data
public class PolicyWithGradeRow {

    private Long policySeq;
    private Long custSeq;
    private String gradeCd;
}
