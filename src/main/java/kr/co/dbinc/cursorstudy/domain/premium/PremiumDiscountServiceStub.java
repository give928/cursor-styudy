package kr.co.dbinc.cursorstudy.domain.premium;

import java.math.BigDecimal;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * {@link PremiumDiscountService}의 임시 구현체.
 * <p>본 교육 세션6 이전까지는 항상 0%를 반환한다.</p>
 */
@Primary
@Service
public class PremiumDiscountServiceStub implements PremiumDiscountService {

    /**
     * 할인율을 계산한다(스텁: 항상 0).
     *
     * @param gradeCd 등급 코드(스텁에서는 미사용)
     * @return {@link BigDecimal#ZERO}
     */
    @Override
    public BigDecimal calculateDiscountRate(String gradeCd) {
        return BigDecimal.ZERO;
    }
}
