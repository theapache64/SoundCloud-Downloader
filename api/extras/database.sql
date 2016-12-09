DROP DATABASE IF EXISTS scd;
CREATE DATABASE scd;
USE scd;

CREATE TABLE `users`(
  id INT(11) NOT NULL AUTO_INCREMENT,
  email VARCHAR(100) NOT NULL,
  api_key VARCHAR (10) NOT NULL,
  is_active TINYINT(4)  NOT NULL  DEFAULT 1 ,
  PRIMARY KEY (id)
);

INSERT INTO users (id,email,api_key) VALUES ('1','theapache64@gmail.com','abcd1234');

CREATE TABLE `requests`(
  id INT(11) NOT NULL AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  client
  sound_cloud_url TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE `preference` (
  `id`     INT(11)      NOT NULL AUTO_INCREMENT,
  `_key`   VARCHAR(100) NOT NULL,
  `_value` TEXT         NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `_key` (`_key`)
);

INSERT INTO preference (_key, _value) VALUES
  ('default_user_id', '1'),
  ('gmail_username', 'mymailer64@gmail.com'),
  ('gmail_password', 'mypassword64'),
  ('admin_email', 'theapache64@gmail.com');

CREATE TABLE clients(
  `id`     INT(11)      NOT NULL AUTO_INCREMENT,
  `_key`   VARCHAR(100) NOT NULL,
  `_value` TEXT         NOT NULL,
  PRIMARY KEY (`id`)
);