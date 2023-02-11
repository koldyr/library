CREATE USER trx_user password 'xxx';

GRANT SELECT, INSERT, UPDATE, DELETE ON T_AUTHOR TO trx_user;
GRANT SELECT, UPDATE ON SEQ_AUTHOR TO trx_user;

GRANT SELECT, INSERT, UPDATE, DELETE ON T_BOOK TO trx_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_BOOK_GENRE TO trx_user;
GRANT SELECT, UPDATE ON SEQ_BOOK TO trx_user;

GRANT SELECT, INSERT, UPDATE, DELETE ON T_READER TO trx_user;
GRANT SELECT, UPDATE ON SEQ_READER TO trx_user;

GRANT SELECT, INSERT, UPDATE, DELETE ON T_READER_ROLES TO trx_user;

GRANT SELECT, INSERT, UPDATE, DELETE ON T_ORDER TO trx_user;
GRANT SELECT, UPDATE ON SEQ_ORDER TO trx_user;

GRANT SELECT, INSERT, UPDATE, DELETE ON T_FEEDBACK TO trx_user;
GRANT SELECT, UPDATE ON SEQ_FEEDBACK TO trx_user;

GRANT SELECT ON T_ROLE TO trx_user;
GRANT SELECT ON T_PRIVILEGE TO trx_user;
GRANT SELECT ON T_ROLE_PRIVILEGES TO trx_user;
GRANT SELECT ON T_GENRE TO trx_user;
