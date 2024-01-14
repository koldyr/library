create index idx_book_author on t_book (author_id);
create index idx_author_names on t_author (first_name, last_name);
create index idx_feedback_reader on t_feedback (reader_id);
create index idx_order_reader on t_order (reader_id);
create index idx_order_book on t_order (book_id);
