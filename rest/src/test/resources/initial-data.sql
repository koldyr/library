insert into T_READER (READER_ID, FIRST_NAME, LAST_NAME, MAIL, ADDRESS, PHONE_NUMBER, PASSWORD)
values (next value for SEQ_READER, 'koldyr', 'koldyr', 'me@koldyr.com', 'Minsk', '+375297777777', '$2a$10$O.lTfOYmXq6rjeiBuTt3weq0UJJjSRKcd2aLciHvdIJNQmZa.IGHi');

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

insert into T_ROLE (ROLE_ID, ROLE_NAME)
values (2, 'supervisor');

insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 0);
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 1);
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 2);
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 3);
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 4);
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 5);
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 6);
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 7);
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 8);
insert into T_ROLE_PRIVILEGES (ROLE_ID, PRIVILEGE_ID)
values (2, 9);

insert into T_READER_ROLES (READER_ID, ROLE_ID)
values (1, 2);

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
