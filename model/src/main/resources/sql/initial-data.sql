insert into T_AUTHOR (AUTHOR_ID, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH)
values (SEQ_AUTHOR.nextval, 'a1_first', 'a1_last', '1990-10-10');

insert into T_BOOK (BOOK_ID, TITLE, PUBLISHING_HOUSE, AUTHOR_ID, PUBLICATION_DATE, GENRE, BOOK_COVER, NOTE, COUNT)
values (SEQ_BOOK.nextval, 'b1_title', 'b1_house', SEQ_AUTHOR.currval, '2021-09-09', 'SCIFI', null, 'b1_note', 10);

insert into T_READER (READER_ID, FIRST_NAME, LAST_NAME, MAIL, ADDRESS, PHONE_NUMBER, PASSWORD)
values (SEQ_READER.nextval, 'koldyr', 'koldyr', 'me@koldyr.com', 'Minsk', '+375297709965', '$2a$10$O.lTfOYmXq6rjeiBuTt3weq0UJJjSRKcd2aLciHvdIJNQmZa.IGHi');

insert into T_ORDER (ORDER_ID, BOOK_ID, READER_ID, ORDERED, RETURNED, NOTES)
values (SEQ_ORDER.nextval, SEQ_BOOK.currval, SEQ_READER.currval, CURRENT_TIMESTAMP(), null, 'o1_note');

insert into T_FEEDBACK (FEEDBACK_ID, READER_ID, BOOK_ID, "DATE", "TEXT", RATE)
values (SEQ_FEEDBACK.nextval, SEQ_READER.currval, SEQ_BOOK.currval, CURRENT_TIMESTAMP(), 'f1_feedback', 8);

insert into T_AUTHORITY (AUTHORITY_ID, GRANTED)
values (0, 'reader');
insert into T_AUTHORITY (AUTHORITY_ID, GRANTED)
values (1, 'librarian');
insert into T_AUTHORITY (AUTHORITY_ID, GRANTED)
values (2, 'supervisor');

insert into T_READER_AUTHORITIES (READER_ID, AUTHORITY_ID)
values (1, 0);
insert into T_READER_AUTHORITIES (READER_ID, AUTHORITY_ID)
values (1, 1);
insert into T_READER_AUTHORITIES (READER_ID, AUTHORITY_ID)
values (1, 2);
