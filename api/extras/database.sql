  DROP DATABASE IF EXISTS scd;
  CREATE DATABASE scd;
  USE scd;

  CREATE TABLE `users`(
    id INT(11) NOT NULL AUTO_INCREMENT,
    name VARCHAR (100) NOT NULL,
    email VARCHAR (150) DEFAULT NULL,
    imei VARCHAR(18) NOT NULL,
    device_hash TEXT NOT NULL,
    api_key VARCHAR (10) NOT NULL,
    is_active TINYINT(4)  NOT NULL  DEFAULT 1 ,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at VARCHAR(50) DEFAULT NULL,
    PRIMARY KEY (id),
   UNIQUE KEY (imei)
  );

  CREATE TABLE users_audit(
    id INT(11) NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    column_changed VARCHAR (50) NOT NULL,
    value_changed_from TEXT NOT NULL,
    value_changed_to TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY(user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
  );

  /* Change the delimiter so we can use ";" within the CREATE TRIGGER */
DELIMITER $$

CREATE TRIGGER after_users_update
AFTER UPDATE ON users
FOR EACH ROW BEGIN

  IF OLD.name <> NEW.name
  THEN
    INSERT INTO users_audit
    SET
    user_id = OLD.id,
    column_changed = 'name',
    value_changed_from = OLD.name,
    value_changed_to = NEW.name;
  END IF;

  IF OLD.email <> NEW.email
  THEN
    INSERT INTO users_audit
    SET
    user_id = OLD.id,
    column_changed = 'email',
    value_changed_from = OLD.email,
    value_changed_to = NEW.email;
  END IF;

  IF OLD.imei <> NEW.imei
  THEN
    INSERT INTO users_audit
    SET
    user_id = OLD.id,
    column_changed = 'imei',
    value_changed_from = OLD.imei,
    value_changed_to = NEW.imei;
  END IF;

   IF OLD.device_hash <> NEW.device_hash
  THEN
    INSERT INTO users_audit
    SET
    user_id = OLD.id,
    column_changed = 'device_hash',
    value_changed_from = OLD.device_hash,
    value_changed_to = NEW.device_hash;
  END IF;

   IF OLD.api_key <> NEW.api_key
  THEN
    INSERT INTO users_audit
    SET
    user_id = OLD.id,
    column_changed = 'api_key',
    value_changed_from = OLD.api_key,
    value_changed_to = NEW.api_key;
  END IF;

END$$

DELIMITER ;


  CREATE TABLE `requests`(
    id INT(11) NOT NULL AUTO_INCREMENT,
    user_id INT(11) NOT NULL,
    soundcloud_url TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
  );

   CREATE TABLE tracks(
    id INT NOT NULL AUTO_INCREMENT,
    request_id INT NOT NULL,
    soundcloud_url TEXT NOT NULL,
    soundcloud_track_id BIGINT NOT NULL,
    title TEXT NOT NULL,
    duration BIGINT NOT NULL,
    username VARCHAR (255) NOT NULL,
    artwork_url TEXT,
    filename TEXT NOT NULL,
    original_format VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(id),
    FOREIGN KEY (request_id) REFERENCES requests(id) ON UPDATE CASCADE ON DELETE CASCADE,
    UNIQUE KEY(soundcloud_track_id)
   );

   CREATE TABLE download_requests(
      id INT NOT NULL AUTO_INCREMENT,
      track_id INT NOT NULL,
      request_id INT NOT NULL,
      download_url TEXT NOT NULL,
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      PRIMARY KEY(id),
      FOREIGN KEY (track_id) REFERENCES tracks(id) ON UPDATE CASCADE ON DELETE CASCADE,
      FOREIGN KEY (request_id) REFERENCES requests(id) ON UPDATE CASCADE ON DELETE CASCADE
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
    ('admin_email', 'theapache64@gmail.com'),
    ('is_debug_download', '0'),
    ('filename_format', '%s_theah64.%s'),
    ('apk_url', 'https://github.com/theapache64/SoundCloud-Downloader/releases/download/v1.0/soundclouddownloader.apk'),
    ('is_new_soundcloud_downloader', '1'),
    ('is_open_api', '0'),
    ('is_direct_download', '0');


