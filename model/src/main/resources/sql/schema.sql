CREATE TABLE IF NOT EXISTS T_BOOK
(
    book_id          INTEGER      NOT NULL,
    title            VARCHAR(255) NOT NULL,
    publishing_house VARCHAR(255),
    author_id        INTEGER,
    publication_date date,
    genre            VARCHAR(32),
    book_cover       VARCHAR(1024),
    note             VARCHAR(1024)
);
alter table T_BOOK
    add constraint PK_BOOK PRIMARY KEY (BOOK_ID);
create sequence SEQ_BOOK start with 1;

CREATE TABLE T_READER
(
    reader_id    INTEGER      NOT NULL,
    first_name   VARCHAR(255) NOT NULL,
    last_name    VARCHAR(255),
    mail         VARCHAR(255) NOT NULL,
    address      VARCHAR(255),
    phone_number VARCHAR(255),
    note         VARCHAR(255)
);
alter table T_READER
    add constraint PK_READER PRIMARY KEY (READER_ID);
create sequence SEQ_READER start with 1;

CREATE TABLE IF NOT EXISTS T_ORDER
(
    order_id  INTEGER   NOT NULL,
    book_id   INTEGER   NOT NULL,
    reader_id INTEGER   NOT NULL,
    ordered   TIMESTAMP NOT NULL,
    returned  TIMESTAMP,
    notes     VARCHAR(255)
);
alter table T_ORDER
    add constraint PK_ORDER PRIMARY KEY (ORDER_ID);
create sequence SEQ_ORDER start with 1;

CREATE TABLE T_READER_ORDERS (
    orders_order_id INTEGER   NOT NULL,
    reader_reader_id INTEGER   NOT NULL
);


CREATE TABLE T_AUTHOR
(
    author_id     INTEGER      NOT NULL,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255),
    date_of_birth DATE
);
alter table T_AUTHOR
    add constraint PK_AUTHOR PRIMARY KEY (AUTHOR_ID);
create sequence SEQ_AUTHOR start with 1;

CREATE TABLE T_FEEDBACK
(
    feedback_id INTEGER    NOT NULL,
    reader_id   INTEGER    NOT NULL,
    book_id     INTEGER    NOT NULL,
    "DATE"      TIMESTAMP  NOT NULL,
    "TEXT"      VARCHAR(255),
    rate        INTEGER(1) NOT NULL
);
alter table T_FEEDBACK
    add constraint PK_FEEDBACK PRIMARY KEY (FEEDBACK_ID);
create sequence SEQ_FEEDBACK start with 1;

ALTER TABLE T_BOOK
    ADD CONSTRAINT FK_BOOK_AUTHOR FOREIGN KEY (AUTHOR_ID) REFERENCES T_AUTHOR (AUTHOR_ID);

ALTER TABLE T_ORDER
    ADD CONSTRAINT FK_ORDER_BOOK FOREIGN KEY (book_id) REFERENCES T_BOOK (book_id);

ALTER TABLE T_ORDER
    ADD CONSTRAINT FK_ORDER_READER FOREIGN KEY (reader_id) REFERENCES T_READER (reader_id);

ALTER TABLE T_FEEDBACK
    ADD CONSTRAINT FK_FEEDBACK_BOOK FOREIGN KEY (book_id) REFERENCES T_BOOK (book_id);

ALTER TABLE T_FEEDBACK
    ADD CONSTRAINT FK_FEEDBACK_READER FOREIGN KEY (reader_id) REFERENCES T_READER (reader_id);
