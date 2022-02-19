insert into T_READER (READER_ID, FIRST_NAME, LAST_NAME, MAIL, ADDRESS, PHONE_NUMBER, PASSWORD)
values (NEXTVAL('SEQ_READER'), 'koldyr', 'koldyr', 'me@koldyr.com', 'Minsk', '+375297777777', '$2a$10$O.lTfOYmXq6rjeiBuTt3weq0UJJjSRKcd2aLciHvdIJNQmZa.IGHi');

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
values (CURRVAL('SEQ_READER'), 1);
insert into T_READER_ROLES (READER_ID, ROLE_ID)
values (CURRVAL('SEQ_READER'), 2);

