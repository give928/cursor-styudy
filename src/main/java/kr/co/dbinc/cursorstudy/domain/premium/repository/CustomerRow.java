package kr.co.dbinc.cursorstudy.domain.premium.repository;

import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TB_CUSTOMER 존재 여부 확인용 조회 행.
 *
 * <p>null 여부로 고객 존재를 판단하며, 필드는 MyBatis 매핑 전용이다.</p>
 * <p>{@code @Setter}/{@code @NoArgsConstructor}: MyBatis resultType 매핑을 위해 허용한다.</p>
 */
@Setter
@NoArgsConstructor
public class CustomerRow {

    private Long custSeq;
}
