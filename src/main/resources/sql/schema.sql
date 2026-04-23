-- ============================================================
-- H2 파일 모드 스키마 (Oracle 호환 모드 사용)
-- 기존 schema.sql 대비 변경점:
--   - CREATE SEQUENCE: NOCACHE 제거 (H2 미지원 키워드)
--   - COMMENT ON: 제거 (H2 Oracle 모드 미지원)
--   - DROP IF EXISTS 추가 (앱 재시작 시 재생성 방지)
-- ============================================================

-- ============================================================
-- 시퀀스
-- ============================================================
CREATE SEQUENCE IF NOT EXISTS SEQ_CODE         START WITH 1     INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS SEQ_AGENT        START WITH 100   INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS SEQ_CUSTOMER     START WITH 1000  INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS SEQ_PRODUCT      START WITH 100   INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS SEQ_POLICY       START WITH 10000 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS SEQ_CLAIM        START WITH 5000  INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS SEQ_CLAIM_DTL    START WITH 1     INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS SEQ_PAYMENT      START WITH 1     INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS SEQ_NOTIFICATION START WITH 1     INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS SEQ_AUDIT_LOG    START WITH 1     INCREMENT BY 1;

-- ============================================================
-- TB_CODE_MASTER : 공통 코드
-- ============================================================
CREATE TABLE IF NOT EXISTS TB_CODE_MASTER (
                                              CODE_SEQ     NUMBER(10)    NOT NULL,
    CODE_GRP     VARCHAR2(30)  NOT NULL,
    CODE_CD      VARCHAR2(30)  NOT NULL,
    CODE_NM      VARCHAR2(100) NOT NULL,
    CODE_DESC    VARCHAR2(200),
    SORT_ORD     NUMBER(5)     DEFAULT 0,
    USE_YN       CHAR(1)       DEFAULT 'Y' NOT NULL,
    REG_DTM      DATE          DEFAULT SYSDATE NOT NULL,
    REG_ID       VARCHAR2(50)  NOT NULL,
    UPD_DTM      DATE          DEFAULT SYSDATE NOT NULL,
    UPD_ID       VARCHAR2(50)  NOT NULL,
    CONSTRAINT PK_CODE_MASTER PRIMARY KEY (CODE_SEQ),
    CONSTRAINT UQ_CODE_MASTER UNIQUE (CODE_GRP, CODE_CD),
    CONSTRAINT CK_CODE_USE_YN CHECK (USE_YN IN ('Y', 'N'))
    );

-- ============================================================
-- TB_AGENT : 설계사
-- ============================================================
CREATE TABLE IF NOT EXISTS TB_AGENT (
                                        AGENT_SEQ    NUMBER(10)    NOT NULL,
    AGENT_NO     VARCHAR2(20)  NOT NULL,
    AGENT_NM     VARCHAR2(100) NOT NULL,
    TEL_NO       VARCHAR2(20),
    EMAIL        VARCHAR2(100),
    USE_YN       CHAR(1)       DEFAULT 'Y' NOT NULL,
    REG_DTM      DATE          DEFAULT SYSDATE NOT NULL,
    REG_ID       VARCHAR2(50)  NOT NULL,
    UPD_DTM      DATE          DEFAULT SYSDATE NOT NULL,
    UPD_ID       VARCHAR2(50)  NOT NULL,
    CONSTRAINT PK_AGENT PRIMARY KEY (AGENT_SEQ),
    CONSTRAINT UQ_AGENT_NO UNIQUE (AGENT_NO),
    CONSTRAINT CK_AGENT_USE_YN CHECK (USE_YN IN ('Y', 'N'))
    );

-- ============================================================
-- TB_CUSTOMER : 고객
-- ============================================================
CREATE TABLE IF NOT EXISTS TB_CUSTOMER (
                                           CUST_SEQ      NUMBER(10)    NOT NULL,
    CUST_NO       VARCHAR2(20)  NOT NULL,
    CUST_NM       VARCHAR2(100) NOT NULL,
    CUST_TYPE     VARCHAR2(10)  DEFAULT 'I' NOT NULL,
    PHONE_NO      VARCHAR2(20),
    EMAIL         VARCHAR2(200),
    GRADE_CD      VARCHAR2(10)  DEFAULT 'BASIC' NOT NULL,
    GRADE_CHG_DTM DATE          DEFAULT SYSDATE,
    USE_YN        VARCHAR2(1)   DEFAULT 'Y' NOT NULL,
    REG_DTM       DATE          DEFAULT SYSDATE NOT NULL,
    REG_ID        VARCHAR2(50)  DEFAULT 'SYSTEM' NOT NULL,
    UPD_DTM       DATE          DEFAULT SYSDATE NOT NULL,
    UPD_ID        VARCHAR2(50)  DEFAULT 'SYSTEM' NOT NULL,
    CONSTRAINT PK_CUSTOMER PRIMARY KEY (CUST_SEQ),
    CONSTRAINT UK_CUST_NO UNIQUE (CUST_NO),
    CONSTRAINT CK_CUST_GRADE CHECK (GRADE_CD IN ('BASIC', 'SILVER', 'GOLD', 'VIP'))
    );

-- ============================================================
-- TB_PRODUCT : 보험 상품
-- ============================================================
CREATE TABLE IF NOT EXISTS TB_PRODUCT (
                                          PROD_SEQ         NUMBER(10)    NOT NULL,
    PROD_CD          VARCHAR2(20)  NOT NULL,
    PROD_NM          VARCHAR2(200) NOT NULL,
    PROD_TYPE        VARCHAR2(30),
    BASE_PREMIUM     NUMBER(15,2)  NOT NULL,
    COVERAGE_AMT     NUMBER(15,2)  NOT NULL,
    CONTRACT_PERIOD  NUMBER(5)     NOT NULL,
    USE_YN           CHAR(1)       DEFAULT 'Y' NOT NULL,
    REG_DTM          DATE          DEFAULT SYSDATE NOT NULL,
    REG_ID           VARCHAR2(50)  NOT NULL,
    UPD_DTM          DATE          DEFAULT SYSDATE NOT NULL,
    UPD_ID           VARCHAR2(50)  NOT NULL,
    CONSTRAINT PK_PRODUCT PRIMARY KEY (PROD_SEQ),
    CONSTRAINT UQ_PROD_CD UNIQUE (PROD_CD),
    CONSTRAINT CK_PROD_USE_YN CHECK (USE_YN IN ('Y', 'N'))
    );

-- ============================================================
-- TB_DISCOUNT_POLICY : 등급별 할인 정책
-- ============================================================
CREATE TABLE IF NOT EXISTS TB_DISCOUNT_POLICY (
                                                  GRADE_CD             VARCHAR2(10)  NOT NULL,
    DISCOUNT_RATE        NUMBER(5,4)   NOT NULL,
    MIN_CONTRACT_YEAR    NUMBER(3)     NOT NULL,
    NO_CLAIM_REQUIRED    VARCHAR2(1)   DEFAULT 'N' NOT NULL,
    MIN_ANNUAL_PREMIUM   NUMBER(15,2)  DEFAULT 0,
    APPLY_START_DTM      DATE          DEFAULT SYSDATE NOT NULL,
    APPLY_END_DTM        DATE,
    CONSTRAINT PK_DISCOUNT_POLICY PRIMARY KEY (GRADE_CD)
    );

-- ============================================================
-- TB_POLICY : 보험 계약
-- ============================================================
CREATE TABLE IF NOT EXISTS TB_POLICY (
                                         POLICY_SEQ      NUMBER(10)    NOT NULL,
    POLICY_NO       VARCHAR2(30)  NOT NULL,
    CUST_SEQ        NUMBER(10)    NOT NULL,
    PROD_SEQ        NUMBER(10)    NOT NULL,
    AGENT_SEQ       NUMBER(10),
    STATUS_CD       VARCHAR2(10)  DEFAULT 'ACTIVE' NOT NULL,
    CONTRACT_DTM    DATE          DEFAULT SYSDATE NOT NULL,
    START_DTM       DATE          NOT NULL,
    EXPIRE_DTM      DATE          NOT NULL,
    PAYMENT_CYCLE   VARCHAR2(1)   DEFAULT 'M' NOT NULL,
    MONTHLY_PREMIUM NUMBER(15,2)  NOT NULL,
    ANNUAL_PREMIUM  NUMBER(15,2)  NOT NULL,
    AUTO_RENEW_YN   VARCHAR2(1)   DEFAULT 'N' NOT NULL,
    INSURED_NM      VARCHAR2(100) NOT NULL,
    COVERAGE_AMT    NUMBER(15,2)  NOT NULL,
    REG_DTM         DATE          DEFAULT SYSDATE NOT NULL,
    REG_ID          VARCHAR2(50)  DEFAULT 'SYSTEM' NOT NULL,
    UPD_DTM         DATE          DEFAULT SYSDATE NOT NULL,
    UPD_ID          VARCHAR2(50)  DEFAULT 'SYSTEM' NOT NULL,
    CONSTRAINT PK_POLICY PRIMARY KEY (POLICY_SEQ),
    CONSTRAINT UK_POLICY_NO UNIQUE (POLICY_NO),
    CONSTRAINT FK_POLICY_CUST FOREIGN KEY (CUST_SEQ) REFERENCES TB_CUSTOMER(CUST_SEQ)
    );

-- ============================================================
-- TB_CLAIM : 클레임 (보험금 청구)
-- ============================================================
CREATE TABLE IF NOT EXISTS TB_CLAIM (
                                        CLAIM_SEQ       NUMBER(10)    NOT NULL,
    CLAIM_NO        VARCHAR2(30)  NOT NULL,
    POLICY_SEQ      NUMBER(10)    NOT NULL,
    CLAIM_TYPE      VARCHAR2(20)  NOT NULL,
    STATUS_CD       VARCHAR2(10)  DEFAULT 'RECEIPT' NOT NULL,
    ACCIDENT_DTM    DATE          NOT NULL,
    RECEIPT_DTM     DATE          DEFAULT SYSDATE NOT NULL,
    REVIEW_DTM      DATE,
    CLAIM_AMT       NUMBER(15,2)  NOT NULL,
    APPROVED_AMT    NUMBER(15,2)  DEFAULT 0,
    DISCOUNT_RATE   NUMBER(5,4)   DEFAULT 0,
    GRADE_CD        VARCHAR2(10),
    REJECT_REASON   VARCHAR2(2000),
    REG_DTM         DATE          DEFAULT SYSDATE NOT NULL,
    REG_ID          VARCHAR2(50)  DEFAULT 'SYSTEM' NOT NULL,
    UPD_DTM         DATE          DEFAULT SYSDATE NOT NULL,
    UPD_ID          VARCHAR2(50)  DEFAULT 'SYSTEM' NOT NULL,
    CONSTRAINT PK_CLAIM        PRIMARY KEY (CLAIM_SEQ),
    CONSTRAINT UK_CLAIM_NO     UNIQUE (CLAIM_NO),
    CONSTRAINT FK_CLAIM_POLICY FOREIGN KEY (POLICY_SEQ) REFERENCES TB_POLICY(POLICY_SEQ)
    );

-- ============================================================
-- TB_CLAIM_DETAIL : 클레임 상세 항목
-- ============================================================
CREATE TABLE IF NOT EXISTS TB_CLAIM_DETAIL (
                                               DETAIL_SEQ    NUMBER(10)    NOT NULL,
    CLAIM_SEQ     NUMBER(10)    NOT NULL,
    ITEM_CD       VARCHAR2(30)  NOT NULL,
    ITEM_NM       VARCHAR2(200) NOT NULL,
    ITEM_AMT      NUMBER(15,2)  NOT NULL,
    APPROVE_AMT   NUMBER(15,2)  DEFAULT 0,
    REMARK        VARCHAR2(500),
    REG_DTM       DATE          DEFAULT SYSDATE NOT NULL,
    REG_ID        VARCHAR2(50)  NOT NULL,
    UPD_DTM       DATE          DEFAULT SYSDATE NOT NULL,
    UPD_ID        VARCHAR2(50)  NOT NULL,
    CONSTRAINT PK_CLAIM_DETAIL PRIMARY KEY (DETAIL_SEQ),
    CONSTRAINT FK_CLAIM_DETAIL_CLAIM FOREIGN KEY (CLAIM_SEQ) REFERENCES TB_CLAIM(CLAIM_SEQ)
    );

-- ============================================================
-- TB_PAYMENT : 납입 이력
-- ============================================================
CREATE TABLE IF NOT EXISTS TB_PAYMENT (
                                          PAYMENT_SEQ    NUMBER(10)   NOT NULL,
    POLICY_SEQ     NUMBER(10)   NOT NULL,
    DUE_DTM        DATE         NOT NULL,
    PAY_DTM        DATE,
    PAY_AMT        NUMBER(15,2) NOT NULL,
    PAY_STATUS     VARCHAR2(20) DEFAULT 'PENDING' NOT NULL,
    PAY_METHOD     VARCHAR2(20),
    PAYMENT_CYCLE  CHAR(1)      NOT NULL,
    REG_DTM        DATE         DEFAULT SYSDATE NOT NULL,
    REG_ID         VARCHAR2(50) NOT NULL,
    UPD_DTM        DATE         DEFAULT SYSDATE NOT NULL,
    UPD_ID         VARCHAR2(50) NOT NULL,
    CONSTRAINT PK_PAYMENT PRIMARY KEY (PAYMENT_SEQ),
    CONSTRAINT FK_PAYMENT_POLICY FOREIGN KEY (POLICY_SEQ) REFERENCES TB_POLICY(POLICY_SEQ),
    CONSTRAINT CK_PAY_STATUS CHECK (PAY_STATUS IN ('PENDING','PAID','OVERDUE')),
    CONSTRAINT CK_PAY_CYCLE  CHECK (PAYMENT_CYCLE IN ('M','Q','S','Y'))
    );

-- ============================================================
-- TB_NOTIFICATION : 알림 발송 이력
-- ============================================================
CREATE TABLE IF NOT EXISTS TB_NOTIFICATION (
                                               NOTI_SEQ      NUMBER(10)    NOT NULL,
    NOTI_TYPE     VARCHAR2(30)  NOT NULL,
    TARGET_TYPE   VARCHAR2(20)  NOT NULL,
    TARGET_SEQ    NUMBER(10)    NOT NULL,
    CUST_SEQ      NUMBER(10)    NOT NULL,
    SEND_CHANNEL  VARCHAR2(10)  NOT NULL,
    SEND_STATUS   VARCHAR2(20)  DEFAULT 'PENDING' NOT NULL,
    SEND_DTM      DATE,
    NOTI_MSG      VARCHAR2(2000) NOT NULL,
    REG_DTM       DATE          DEFAULT SYSDATE NOT NULL,
    REG_ID        VARCHAR2(50)  NOT NULL,
    UPD_DTM       DATE          DEFAULT SYSDATE NOT NULL,
    UPD_ID        VARCHAR2(50)  NOT NULL,
    CONSTRAINT PK_NOTIFICATION PRIMARY KEY (NOTI_SEQ),
    CONSTRAINT FK_NOTI_CUST FOREIGN KEY (CUST_SEQ) REFERENCES TB_CUSTOMER(CUST_SEQ),
    CONSTRAINT CK_NOTI_CHANNEL CHECK (SEND_CHANNEL IN ('EMAIL','SMS')),
    CONSTRAINT CK_NOTI_STATUS  CHECK (SEND_STATUS IN ('PENDING','SENT','FAILED'))
    );

-- ============================================================
-- TB_AUDIT_LOG : 감사 로그
-- ============================================================
CREATE TABLE IF NOT EXISTS TB_AUDIT_LOG (
                                            LOG_SEQ       NUMBER(10)    NOT NULL,
    ACTION_CD     VARCHAR2(30)  NOT NULL,
    TARGET_TABLE  VARCHAR2(50)  NOT NULL,
    TARGET_SEQ    NUMBER(10)    NOT NULL,
    BEFORE_VALUE  CLOB,
    AFTER_VALUE   CLOB,
    USER_ID       VARCHAR2(50)  NOT NULL,
    CLIENT_IP     VARCHAR2(50),
    REG_DTM       DATE          DEFAULT SYSDATE NOT NULL,
    REG_ID        VARCHAR2(50)  NOT NULL,
    UPD_DTM       DATE          DEFAULT SYSDATE NOT NULL,
    UPD_ID        VARCHAR2(50)  NOT NULL,
    CONSTRAINT PK_AUDIT_LOG PRIMARY KEY (LOG_SEQ),
    CONSTRAINT CK_AUDIT_ACTION CHECK (ACTION_CD IN ('INSERT','UPDATE','DELETE'))
    );

-- ============================================================
-- 초기 데이터: 할인 정책
-- ============================================================
MERGE INTO TB_DISCOUNT_POLICY AS T
    USING (VALUES
               ('BASIC',  0.0000, 0, 'N', 0,        SYSDATE, NULL),
               ('SILVER', 0.0300, 1, 'N', 0,        SYSDATE, NULL),
               ('GOLD',   0.0500, 3, 'Y', 0,        SYSDATE, NULL),
               ('VIP',    0.1000, 5, 'Y', 10000000, SYSDATE, NULL)
    ) AS S(GRADE_CD, DISCOUNT_RATE, MIN_CONTRACT_YEAR, NO_CLAIM_REQUIRED, MIN_ANNUAL_PREMIUM, APPLY_START_DTM, APPLY_END_DTM)
    ON T.GRADE_CD = S.GRADE_CD
    WHEN NOT MATCHED THEN
        INSERT (GRADE_CD, DISCOUNT_RATE, MIN_CONTRACT_YEAR, NO_CLAIM_REQUIRED, MIN_ANNUAL_PREMIUM, APPLY_START_DTM, APPLY_END_DTM)
            VALUES (S.GRADE_CD, S.DISCOUNT_RATE, S.MIN_CONTRACT_YEAR, S.NO_CLAIM_REQUIRED, S.MIN_ANNUAL_PREMIUM, S.APPLY_START_DTM, S.APPLY_END_DTM);
