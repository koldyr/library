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
CREATE TABLE IF NOT EXISTS T_GENRE
(
    genre_id   INTEGER      NOT NULL,
    genre_name VARCHAR(255) NOT NULL,
    CONSTRAINT PK_GENRE PRIMARY KEY (genre_id),
    CONSTRAINT UC_GENRE_NAME UNIQUE (genre_name)
);

--
CREATE TABLE IF NOT EXISTS T_BOOK_GENRE
(
    book_id  INTEGER NOT NULL,
    genre_id INTEGER NOT NULL,
    CONSTRAINT PK_BOOK_GENRE PRIMARY KEY (genre_id, book_id),
    CONSTRAINT FK_BOOK_GENRE FOREIGN KEY (book_id) REFERENCES T_BOOK (book_id),
    CONSTRAINT FK_GENRE FOREIGN KEY (genre_id) REFERENCES T_GENRE (genre_id)
);

--
CREATE TABLE T_READER
(
    reader_id    INTEGER      NOT NULL,
    first_name   VARCHAR(255) NOT NULL,
    last_name    VARCHAR(255),
    mail         VARCHAR(255) NOT NULL,
    address      VARCHAR(255),
    phone_number VARCHAR(255),
    note         VARCHAR(255),
    password     VARCHAR(255) NOT NULL,
    CONSTRAINT PK_READER PRIMARY KEY (reader_id),
    CONSTRAINT UC_READER_MAIL UNIQUE (mail)
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
    feedback_id INTEGER   NOT NULL,
    reader_id   INTEGER   NOT NULL,
    book_id     INTEGER   NOT NULL,
    "DATE"      TIMESTAMP NOT NULL,
    "TEXT"      VARCHAR(4000),
    rate        NUMERIC(1) NOT NULL,
    CONSTRAINT PK_FEEDBACK PRIMARY KEY (feedback_id),
    CONSTRAINT FK_FEEDBACK_BOOK FOREIGN KEY (book_id) REFERENCES T_BOOK (book_id),
    CONSTRAINT FK_FEEDBACK_READER FOREIGN KEY (reader_id) REFERENCES T_READER (reader_id)
);
create sequence SEQ_FEEDBACK start with 1;

--
CREATE TABLE T_ROLE
(
    role_id   INTEGER     NOT NULL,
    role_name VARCHAR(32) NOT NULL UNIQUE,
    CONSTRAINT PK_ROLE PRIMARY KEY (role_id)
);

--
CREATE TABLE T_READER_ROLES
(
    reader_id INTEGER NOT NULL,
    role_id   INTEGER NOT NULL,
    CONSTRAINT PK_READER_ROLE PRIMARY KEY (reader_id, role_id),
    CONSTRAINT FK_READER_ROLES FOREIGN KEY (role_id) REFERENCES T_ROLE,
    CONSTRAINT FK_READER_READERS FOREIGN KEY (reader_id) REFERENCES T_READER
);

--
CREATE TABLE T_PRIVILEGE
(
    privilege_id   INTEGER     NOT NULL,
    privilege_name VARCHAR(32) NOT NULL UNIQUE,
    CONSTRAINT PK_PRIVILEGE PRIMARY KEY (privilege_id)
);

--
CREATE TABLE T_ROLE_PRIVILEGES
(
    role_id      INTEGER NOT NULL,
    privilege_id INTEGER NOT NULL,
    CONSTRAINT PK_ROLE_PRIVILEGE PRIMARY KEY (role_id, privilege_id),
    CONSTRAINT FK_ROLE_ROLES FOREIGN KEY (role_id) REFERENCES T_ROLE,
    CONSTRAINT FK_ROLE_PRIVILEGES FOREIGN KEY (privilege_id) REFERENCES T_PRIVILEGE
);
