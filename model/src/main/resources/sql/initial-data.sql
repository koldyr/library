insert into T_AUTHOR (AUTHOR_ID, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH)
values (SEQ_AUTHOR.nextval, 'a1_first', 'a1_last', '1990-10-10');

insert into T_BOOK (BOOK_ID, TITLE, PUBLISHING_HOUSE, AUTHOR_ID, PUBLICATION_DATE, GENRE, BOOK_COVER, NOTE)
values (SEQ_BOOK.nextval, 'b1_title', 'b1_house', SEQ_AUTHOR.currval, '2021-09-09', 'SCIFI', null, 'b1_note');

insert into T_READER (READER_ID, FIRST_NAME, LAST_NAME, MAIL, ADDRESS, PHONE_NUMBER, NOTE)
values (SEQ_READER.nextval, 'r1_first', 'r1_last', 'r1_mail', 'r1_address', 'r1-111-222-333', 'r1_note');

insert into T_ORDER (ORDER_ID, BOOK_ID, READER_ID, ORDERED, RETURNED, NOTES)
values (SEQ_ORDER.nextval, SEQ_BOOK.currval, SEQ_READER.currval, CURRENT_TIMESTAMP() , null, 'o1_note');

insert INTO T_FEEDBACK (FEEDBACK_ID, READER_ID, BOOK_ID, DATE, TEXT, RATE)
VALUES (SEQ_FEEDBACK.nextval, SEQ_READER.currval, SEQ_BOOK.currval, CURRENT_TIMESTAMP(), 'f1_feedback', 8);
