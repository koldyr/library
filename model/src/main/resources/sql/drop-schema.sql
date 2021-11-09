alter table T_BOOK drop constraint FK_BOOK_AUTHOR;
alter table T_ORDER drop constraint FK_ORDER_BOOK;
alter table T_ORDER drop constraint FK_ORDER_READER;
alter table T_FEEDBACK drop constraint FK_FEEDBACK_BOOK;
alter table T_FEEDBACK drop constraint FK_FEEDBACK_READER;

DROP TABLE if exists T_ROLE_PRIVILEGES cascade;
DROP TABLE if exists T_READER_ROLES cascade;
DROP TABLE if exists T_BOOK cascade;
DROP TABLE if exists T_READER cascade;
DROP TABLE if exists T_ORDER cascade;
DROP TABLE if exists T_AUTHOR cascade;
DROP TABLE if exists T_FEEDBACK cascade;
DROP TABLE if exists T_ROLE cascade;
DROP TABLE if exists T_PRIVILEGE cascade;

DROP SEQUENCE SEQ_BOOK;
DROP SEQUENCE SEQ_READER;
DROP SEQUENCE SEQ_ORDER;
DROP SEQUENCE SEQ_AUTHOR;
DROP SEQUENCE SEQ_FEEDBACK;
