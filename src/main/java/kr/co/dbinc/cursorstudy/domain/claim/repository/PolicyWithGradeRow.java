package kr.co.dbinc.cursorstudy.domain.claim.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 계약-고객 등급 조회용 매퍼 행.
 *
 * <p>{@code @Setter}/{@code @NoArgsConstructor}: MyBatis {@code resultType} 매핑 시
 * 기본 생성자 + setter 방식으로 컬럼을 주입하므로 프레임워크 요건상 허용한다.</p>
 */
@Getter
@Setter
@NoArgsConstructor
public class PolicyWithGradeRow {

    private Long policySeq;
    private Long custSeq;
    private String gradeCd;
}
