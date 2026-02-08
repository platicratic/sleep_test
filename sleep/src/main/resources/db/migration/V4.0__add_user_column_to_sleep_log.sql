ALTER TABLE sleep_log
ADD COLUMN user_id BIGINT;

ALTER TABLE sleep_log
ADD CONSTRAINT fk_sleep_users
FOREIGN KEY (user_id)
REFERENCES users(id);