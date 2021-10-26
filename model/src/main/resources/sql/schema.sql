--
CREATE TABLE T_AUTHOR
(
    author_id     INTEGER      NOT NULL,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255),
    date_of_birth DATE,
    constraint PK_AUTHOR PRIMARY KEY (author_id)
);
create sequence SEQ_AUTHOR start with 1;

--
CREATE TABLE IF NOT EXISTS T_BOOK
(
    book_id          INTEGER      NOT NULL,
    title            VARCHAR(255) NOT NULL,
    publishing_house VARCHAR(255),
    author_id        INTEGER,
    publication_date DATE,
    genre            VARCHAR(32),
    book_cover       VARCHAR(1024),
    note             VARCHAR(1024),
    count            INTEGER,
    CONSTRAINT PK_BOOK PRIMARY KEY (book_id),
    CONSTRAINT FK_BOOK_AUTHOR FOREIGN KEY (author_id) REFERENCES T_AUTHOR (author_id)
);
create sequence SEQ_BOOK start with 1;

--
CREATE TABLE T_READER
(
    reader_id    INTEGER      NOT NULL,
    first_name   VARCHAR(255) NOT NULL,
    last_name    VARCHAR(255),
    mail         VARCHAR(255) NOT NULL UNIQUE,
    address      VARCHAR(255),
    phone_number VARCHAR(255),
    note         VARCHAR(255),
    password     VARCHAR(255) NOT NULL,
    CONSTRAINT PK_READER PRIMARY KEY (reader_id)
);
create sequence SEQ_READER start with 1;

--
CREATE TABLE IF NOT EXISTS T_ORDER
(
    order_id  INTEGER   NOT NULL,
    book_id   INTEGER   NOT NULL,
    reader_id INTEGER   NOT NULL,
    ordered   TIMESTAMP NOT NULL,
    returned  TIMESTAMP,
    notes     VARCHAR(255),
    CONSTRAINT PK_ORDER PRIMARY KEY (order_id),
    CONSTRAINT FK_ORDER_BOOK FOREIGN KEY (book_id) REFERENCES T_BOOK (book_id),
    CONSTRAINT FK_ORDER_READER FOREIGN KEY (reader_id) REFERENCES T_READER (reader_id)
);
create sequence SEQ_ORDER start with 1;

--
CREATE TABLE T_FEEDBACK
(
    feedback_id INTEGER    NOT NULL,
    reader_id   INTEGER    NOT NULL,
    book_id     INTEGER    NOT NULL,
    "DATE"      TIMESTAMP  NOT NULL,
    "TEXT"      VARCHAR(255),
    rate        INTEGER(1) NOT NULL,
    CONSTRAINT PK_FEEDBACK PRIMARY KEY (feedback_id),
    CONSTRAINT FK_FEEDBACK_BOOK FOREIGN KEY (book_id) REFERENCES T_BOOK (book_id),
    CONSTRAINT FK_FEEDBACK_READER FOREIGN KEY (reader_id) REFERENCES T_READER (reader_id)
);
create sequence SEQ_FEEDBACK start with 1;

--
CREATE TABLE T_AUTHORITY
(
    authority_id INT         NOT NULL,
    granted      VARCHAR(32) NOT NULL,
    CONSTRAINT PK_AUTHORITY PRIMARY KEY (authority_id)
);

--
CREATE TABLE T_READER_AUTHORITIES
(
    reader_id         INTEGER NOT NULL,
    authority_id INTEGER NOT NULL,
    CONSTRAINT PK_READER_AUTHORITY PRIMARY KEY (reader_id, authority_id),
    CONSTRAINT FK_AUTHORITIES FOREIGN KEY (authority_id) REFERENCES T_AUTHORITY,
    CONSTRAINT FK_READERS FOREIGN KEY (reader_id) REFERENCES T_READER
);
