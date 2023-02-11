insert into T_AUTHOR (AUTHOR_ID, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH)
values (NEXTVAL('SEQ_AUTHOR'), 'a1_first', 'a1_last', '1990-10-10');

insert into T_BOOK (BOOK_ID, TITLE, PUBLISHING_HOUSE, AUTHOR_ID, PUBLICATION_DATE, GENRE, BOOK_COVER, NOTE, COUNT)
values (NEXTVAL('SEQ_BOOK'), 'b1_title', 'b1_house', CURRVAL('SEQ_AUTHOR'), '2021-09-09', 'SCIFI', null, 'b1_note', 10);

insert into T_READER (READER_ID, FIRST_NAME, LAST_NAME, MAIL, ADDRESS, PHONE_NUMBER, PASSWORD)
values (NEXTVAL('SEQ_READER'), 'koldyr', 'koldyr', 'me@koldyr.com', 'Minsk', '+375297777777', '$2a$10$O.lTfOYmXq6rjeiBuTt3weq0UJJjSRKcd2aLciHvdIJNQmZa.IGHi');

insert into T_READER (READER_ID, FIRST_NAME, LAST_NAME, MAIL, ADDRESS, PHONE_NUMBER, PASSWORD)
values (NEXTVAL('SEQ_READER'), 'lemming', 'lemming', 'lemming@koldyr.com', 'Minsk', '+375297777778', '$2a$10$6Ba2ueFmwULa4jz1hFKTyuu/BNCVa3iuLYafpCjEJn1wHxuqQPaxm');

insert into T_READER (READER_ID, FIRST_NAME, LAST_NAME, MAIL, ADDRESS, PHONE_NUMBER, PASSWORD)
values (NEXTVAL('SEQ_READER'), 'shurshun', 'shurshun', 'shurshun@koldyr.com', 'Minsk', '+375297777779', '$2a$10$bCUm/mnYFuMDevURe9vcA.tqUdn3it9gdLZn7cvhpXTK3.qedwcAO');

insert into T_ORDER (ORDER_ID, BOOK_ID, READER_ID, ORDERED, RETURNED, NOTES)
values (NEXTVAL('SEQ_ORDER'), CURRVAL('SEQ_BOOK'), CURRVAL('SEQ_READER'), CURRENT_TIMESTAMP, null, 'o1_note');

insert into T_FEEDBACK (FEEDBACK_ID, READER_ID, BOOK_ID, "DATE", "TEXT", RATE)
values (NEXTVAL('SEQ_FEEDBACK'), CURRVAL('SEQ_READER'), CURRVAL('SEQ_BOOK'), CURRENT_TIMESTAMP, 'f1_feedback', 8);

insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (0, 'FANTASY');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (1, 'SCIFI');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (2, 'HISTORY');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (3, 'ACTION');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (4, 'CLASSICS');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (5, 'COMICS');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (6, 'DETECTIVE');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (7, 'MYSTERY');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (8, 'HORROR');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (9, 'BIOGRAPHY');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (10, 'AUTOBIOGRAPHY');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (11, 'ECONOMICS');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (12, 'COOKBOOK');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (13, 'DIARY');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (14, 'DICTIONARY');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (15, 'CRIME');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (16, 'DRAMA');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (17, 'HUMOR');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (18, 'PHILOSOPHY');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (19, 'POETRY');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (20, 'ROMANCE');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (21, 'THRILLER');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (22, 'WESTERN');
insert into T_GENRE (GENRE_ID, GENRE_NAME)
values (23, 'SCIENCE');

insert into T_ROLE (ROLE_ID, ROLE_NAME)
values (0, 'reader');
insert into T_ROLE (ROLE_ID, ROLE_NAME)
values (1, 'librarian');
insert into T_ROLE (ROLE_ID, ROLE_NAME)
values (2, 'supervisor');

insert into T_PRIVILEGE (PRIVILEGE_ID, PRIVILEGE_NAME)
values (0, 'modify_reader');
insert into T_PRIVILEGE (PRIVILEGE_ID, PRIVILEGE_NAME)
values (1, 'read_reader');
insert into T_PRIVILEGE (PRIVILEGE_ID, PRIVILEGE_NAME)
values (2, 'modify_author');
insert into T_PRIVILEGE (PRIVILEGE_ID, PRIVILEGE_NAME)
values (3, 'read_author');
insert into T_PRIVILEGE (PRIVILEGE_ID, PRIVILEGE_NAME)
values (4, 'modify_book');
insert into T_PRIVILEGE (PRIVILEGE_ID, PRIVILEGE_NAME)
values (5, 'read_book');
insert into T_PRIVILEGE (PRIVILEGE_ID, PRIVILEGE_NAME)
values (6, 'order_book');
insert into T_PRIVILEGE (PRIVILEGE_ID, PRIVILEGE_NAME)
values (7, 'read_order');
insert into T_PRIVILEGE (PRIVILEGE_ID, PRIVILEGE_NAME)
values (8, 'modify_feedback');
insert into T_PRIVILEGE (PRIVILEGE_ID, PRIVILEGE_NAME)
values (9, 'read_feedback');

--READER has PRIVILEGES:
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (0, 0);--modify_reader
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (0, 1);-- read_reader
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (0, 3);-- read_author
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (0, 5);-- read_book
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (0, 6);-- order_book
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (0, 7);-- read_order
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (0, 8);-- make_feedback
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (0, 9);-- read_feedback

--LIBRARIAN has PRIVILEGES:
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (1, 0);--modify_reader
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (1, 1);-- read_reader
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (1, 3);-- read_author
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (1, 5);-- read_book
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (1, 6);-- read_order
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (1, 9);-- read_feedback

--SUPERVISOR has PRIVILEGES:
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 0);--modify_reader
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 1);-- read_reader
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 2);-- modify_author
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 3);-- read_author
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 4);-- modify_book
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 5);-- read_book
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 6);-- order_book
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 7);-- read_order
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 8);-- make_feedback
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 9);-- read_feedback

--koldyr has roles: librarian,supervisor
insert into T_READER_ROLES (READER_ID, ROLE_ID)
values (1, 1);
insert into T_READER_ROLES (READER_ID, ROLE_ID)
values (1, 2);

--lemming has roles: reader
insert into T_READER_ROLES (READER_ID, ROLE_ID)
values (2, 0);

--shurshun has roles: librarian
insert into T_READER_ROLES (READER_ID, ROLE_ID)
values (3, 1);

insert into T_BOOK_GENRE (BOOK_ID, GENRE_ID)
values (CURRVAL('SEQ_BOOK'), 2);
insert into T_BOOK_GENRE (BOOK_ID, GENRE_ID)
values (CURRVAL('SEQ_BOOK'), 21);
insert into T_BOOK_GENRE (BOOK_ID, GENRE_ID)
values (CURRVAL('SEQ_BOOK'), 18);
