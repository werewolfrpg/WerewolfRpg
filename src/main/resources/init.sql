CREATE TABLE IF NOT EXISTS PLAYER_DATA (
    MCID            VARCHAR(36) PRIMARY KEY NOT NULL,
    DCID            BIGINT NOT NULL,
    SCORE           INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS GUILDS (
    GUILDID         BIGINT PRIMARY KEY NOT NULL,
    VCID            BIGINT,
    LOGCHID         BIGINT
);

CREATE TABLE IF NOT EXISTS MATCHES (
    MATCHID         VARCHAR(36) PRIMARY KEY NOT NULL,
    START_TIME      TIMESTAMP,
    END_TIME        TIMESTAMP,
    WINNER_FACTION  VARCHAR(12) NULL
);

CREATE TABLE IF NOT EXISTS RECORDS (
    MATCHID         VARCHAR(36) NOT NULL,
    MCID            VARCHAR(36) NOT NULL,
    PLAYED_ROLE     VARCHAR(9),
    MATCH_RESULT    VARCHAR(12),
    KILLS           INTEGER,
    KILLER          VARCHAR(36) NULL,
    DEATH_CAUSE     VARCHAR(255) NULL,
    BASIC_SKEL      INTEGER,
    LUCKY_SKEL      INTEGER,
    SPECIAL_SKEL    INTEGER,
    EMERALD_DROPS   INTEGER,
    STEAK           INTEGER,
    ASH_USED        INTEGER,
    DIV_USED        INTEGER,
    INVIS_USED      INTEGER,
    SWIFT_USED      INTEGER,
    REVEL_USED      INTEGER,
    GUIDE_USED      INTEGER,
    MSPEAR_USED     INTEGER,
    MSPEAR_CURSES   INTEGER,
    MSPEAR_KILLS    INTEGER,
    TSPEAR_USED     INTEGER,
    TSPEAR_HITS     INTEGER,
    TSPEAR_CURSES   INTEGER,
    TSPEAR_KILLS    INTEGER,
    ARROW_USED      INTEGER,
    ARROW_HITS      INTEGER,
    ARROW_KILLS     INTEGER,
    STUN_USED       INTEGER,
    STUN_HITS       INTEGER,
    STUN_TARGETS    INTEGER,
    STAR_USED       INTEGER,
    STAR_KILLS      INTEGER,
    PROTEC_USED     INTEGER,
    PROTEC_ACTIVE   INTEGER,
    PROTEC_TRIGGER  INTEGER,
    NOTICE_USED     INTEGER,
    NOTICE_TRIGGER  INTEGER,
    AXE_USED        INTEGER,
    AXE_KILLS       INTEGER
);