package kr.co.dbinc.cursorstudy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 애플리케이션 진입점.
 */
@SpringBootApplication
@MapperScan({
        "kr.co.dbinc.cursorstudy.domain.claim.repository",
        "kr.co.dbinc.cursorstudy.domain.premium.repository"
})
public class CursorStudyApplication {

    /**
     * 애플리케이션을 기동한다.
     *
     * @param args 실행 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(CursorStudyApplication.class, args);
    }

}
