CREATE TABLE idbind (
    mcid          INTEGER PRIMARY KEY,
    dcid          INTEGER SECONDARY KEY,
    description VARCHAR(64) NOT NULL,
    completed   BIT NOT NULL);