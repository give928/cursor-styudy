package kr.co.dbinc.cursorstudy.domain.claim.exception;

/**
 * 존재하지 않는 클레임 조회 시 발생하는 예외.
 */
public class ClaimNotFoundException extends RuntimeException {

    /**
     * 클레임 시퀀스를 포함한 메시지로 예외를 생성한다.
     *
     * @param claimSeq 클레임 PK
     */
    public ClaimNotFoundException(Long claimSeq) {
        super("클레임을 찾을 수 없습니다. claimSeq=" + claimSeq);
    }
}
