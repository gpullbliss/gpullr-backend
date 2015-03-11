CREATE USER IF NOT EXISTS "" PASSWORD '' ADMIN;

CREATE SEQUENCE IF NOT EXISTS PUBLIC.SYSTEM_SEQUENCE_79A3ABCD_AC32_4B61_9731_9EA75EE3D359 START WITH 9 BELONGS_TO_TABLE;
CREATE SEQUENCE IF NOT EXISTS PUBLIC.SYSTEM_SEQUENCE_A5458BDB_906E_4DD7_91FA_53B11E88BEA2 START WITH 1 BELONGS_TO_TABLE;
CREATE SEQUENCE IF NOT EXISTS PUBLIC.SYSTEM_SEQUENCE_681B61DE_14D7_43F0_9F8F_1946172AE1BF START WITH 50 BELONGS_TO_TABLE;

CREATE CACHED TABLE IF NOT EXISTS PUBLIC.RANKINGLIST(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_79A3ABCD_AC32_4B61_9731_9EA75EE3D359) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_79A3ABCD_AC32_4B61_9731_9EA75EE3D359,
    CALCULATIONDATE VARCHAR(255) NOT NULL,
    RANKINGSCOPE VARCHAR(255) NOT NULL
);

ALTER TABLE PUBLIC.RANKINGLIST ADD CONSTRAINT IF NOT EXISTS PUBLIC.CONSTRAINT_F PRIMARY KEY(ID);

CREATE CACHED TABLE IF NOT EXISTS PUBLIC.PULLREQUEST(
    ID INTEGER NOT NULL CHECK (ID >= 1),
    CREATEDAT VARCHAR(255),
    FILESCHANGED INTEGER,
    LINESADDED INTEGER,
    LINESREMOVED INTEGER,
    NUMBER INTEGER,
    STATE VARCHAR(255) NOT NULL,
    TITLE VARCHAR(255),
    URL VARCHAR(255),
    ASSIGNEE_ID INTEGER,
    AUTHOR_ID INTEGER NOT NULL,
    REPO_ID INTEGER NOT NULL
);           
ALTER TABLE PUBLIC.PULLREQUEST ADD CONSTRAINT IF NOT EXISTS PUBLIC.CONSTRAINT_E PRIMARY KEY(ID);       
ALTER TABLE PUBLIC.PULLREQUEST ADD COLUMN IF NOT EXISTS ASSIGNEDAT VARCHAR(255);

-- 69 +/- SELECT COUNT(*) FROM PUBLIC.PULLREQUEST;             
CREATE CACHED TABLE IF NOT EXISTS PUBLIC.REPO(
    ID INTEGER NOT NULL CHECK (ID >= 1),
    DESCRIPTION VARCHAR(1000),
    NAME VARCHAR(255) NOT NULL
);     
ALTER TABLE PUBLIC.REPO ADD CONSTRAINT IF NOT EXISTS PUBLIC.CONSTRAINT_2 PRIMARY KEY(ID);    
-- 66 +/- SELECT COUNT(*) FROM PUBLIC.REPO;    
CREATE CACHED TABLE IF NOT EXISTS PUBLIC.USER(
    ID INTEGER NOT NULL,
    AVATARURL VARCHAR(255),
    CANLOGIN BOOLEAN,
    USERNAME VARCHAR(255) NOT NULL
);              
ALTER TABLE PUBLIC.USER ADD CONSTRAINT IF NOT EXISTS PUBLIC.CONSTRAINT_27 PRIMARY KEY(ID);
-- 19 +/- SELECT COUNT(*) FROM PUBLIC.USER;
CREATE CACHED TABLE IF NOT EXISTS PUBLIC.RANKINGLIST_RANKINGS(
    RANKINGLIST_ID BIGINT NOT NULL,
    USERNAME VARCHAR(255),
    AVATARURL VARCHAR(255),
    CLOSEDCOUNT BIGINT,
    RANK INTEGER
);    

CREATE CACHED TABLE IF NOT EXISTS PUBLIC.CLOSEDPULLREQUEST(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_681B61DE_14D7_43F0_9F8F_1946172AE1BF) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_681B61DE_14D7_43F0_9F8F_1946172AE1BF,
    CLOSEDATE VARCHAR(255) NOT NULL,
    PULLREQUESTURL VARCHAR(255) NOT NULL,
    USER_ID INTEGER NOT NULL
);
ALTER TABLE PUBLIC.CLOSEDPULLREQUEST ADD CONSTRAINT IF NOT EXISTS PUBLIC.CONSTRAINT_6 PRIMARY KEY(ID);


ALTER TABLE PUBLIC.REPO ADD CONSTRAINT IF NOT EXISTS PUBLIC.UK_QEDS8S1RPG0DD2O7O4HQJ5T9P UNIQUE(NAME);       
ALTER TABLE PUBLIC.USER ADD CONSTRAINT IF NOT EXISTS PUBLIC.UK_JREODF78A7PL5QIDFH43AXDFB UNIQUE(USERNAME);   
ALTER TABLE PUBLIC.PULLREQUEST ADD CONSTRAINT IF NOT EXISTS PUBLIC.FK_BX0S0OC7KKG0QI43E4MO2FTCI FOREIGN KEY(AUTHOR_ID) REFERENCES PUBLIC.USER(ID) NOCHECK;   
ALTER TABLE PUBLIC.PULLREQUEST ADD CONSTRAINT IF NOT EXISTS PUBLIC.FK_AQ6TYEWWQ4E4S1U03XW06Q2XC FOREIGN KEY(REPO_ID) REFERENCES PUBLIC.REPO(ID) NOCHECK;     
ALTER TABLE PUBLIC.PULLREQUEST ADD CONSTRAINT IF NOT EXISTS PUBLIC.FK_MWCA9DCM3DP9MXMNDVY7Y5287 FOREIGN KEY(ASSIGNEE_ID) REFERENCES PUBLIC.USER(ID) NOCHECK;

ALTER TABLE PUBLIC.RANKINGLIST_RANKINGS ADD CONSTRAINT IF NOT EXISTS PUBLIC.FK_EMFDMDTC71X5V8YP9ULTYGS0D FOREIGN KEY(RANKINGLIST_ID) REFERENCES PUBLIC.RANKINGLIST(ID) NOCHECK;

ALTER TABLE PUBLIC.CLOSEDPULLREQUEST ADD CONSTRAINT IF NOT EXISTS PUBLIC.UK_56FGE1OF1HGXN09EAGKNLI9HN UNIQUE(PULLREQUESTURL);
ALTER TABLE PUBLIC.CLOSEDPULLREQUEST ADD CONSTRAINT IF NOT EXISTS PUBLIC.FK_L1B0V7PRAJ6RFN41L2C0BNWMB FOREIGN KEY(USER_ID) REFERENCES PUBLIC.USER(ID) NOCHECK;