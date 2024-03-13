/**
    This script will be updated during the development process according to the project requirements,
    please re-run this script after the update

 */
Drop SCHEMA  IF EXISTS audioEmail;
create schema audioEmail;

DROP TABLE IF EXISTS audioEmail.mail_info;
create table audioEmail.mail_info(
                             mail_id  INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
                             message_id varchar(128),
                             to_user varchar(128),
                             from_user varchar(128),
                             subject	  varchar(256),
                             content    LONGTEXT,
                             read_flag  char(1) COMMENT '0:unread, 1: read',
                             create_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             createby       VARCHAR(64) DEFAULT 'admin',
                             delete_flag  CHAR(1) DEFAULT '0',
                             delete_time TIMESTAMP,
                             deleteby VARCHAR(64)
) DEFAULT CHARSET=utf8;




DROP TABLE IF EXISTS audioEmail.mail_file;
create table	audioEmail.mail_file(
                             mail_file_id  INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
                             mail_id 	INTEGER,
                             file_name  varchar(128),
                             file_size    varchar(64),
                             file_url varchar(128)
) DEFAULT CHARSET=utf8;


ALTER TABLE audioEmail.mail_file ADD CONSTRAINT fk_mail_id
    FOREIGN KEY (mail_id) REFERENCES audioEmail.mail_info(mail_id);


DROP TABLE IF EXISTS audioEmail.system_account;
create table	audioEmail.system_account(
                                  userId  INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
                                  address varchar(128),
                                  password  varchar(128),
                                  username varchar(128),
                                  smtp 	varchar(64),
                                  imap    varchar(64),
                                  accountType   char(1) COMMENT '0:work, 1: personal',
                                  deviceId varchar(128)
) DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS audioEmail.device;
create table	audioEmail.device(
                          device_id  INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
                          user_id INTEGER,
                          device_content varchar(257)
) DEFAULT CHARSET=utf8;



ALTER TABLE audioEmail.device ADD CONSTRAINT user_id
    FOREIGN KEY (user_id) REFERENCES audioEmail.system_account(userId);



DROP TABLE IF EXISTS audioEmail.tts_info;
create table	audioEmail.tts_info(
                                       tts_id  INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
                                       mail_id INTEGER,
                                       content LONGTEXT,
                                       createby       VARCHAR(64) DEFAULT 'TTS'

) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS audioEmail.account_schedule;
create table	audioEmail.account_schedule(
                                       schedule_id  INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
                                       userId INTEGER,
                                       schedule_time  TIME(2),
                                       voice_type varchar(64)

) DEFAULT CHARSET=utf8;
ALTER TABLE audioEmail.account_schedule ADD CONSTRAINT userId
    FOREIGN KEY (userId) REFERENCES audioEmail.system_account(userId);


ALTER TABLE audioEmail.tts_info ADD CONSTRAINT mail_id
    FOREIGN KEY (mail_id) REFERENCES audioEmail.mail_info(mail_id);


INSERT INTO audioemail.system_account (userId, address, password, username, smtp, imap, accountType, deviceId) VALUES (1, 'crowds@gmail.com', 'zwxqqjkrakhfoucy', 'Crowds', 'smtp.gmail.com', 'imap.gmail.com', '0', '0');

INSERT INTO audioemail.system_account (userId, address, password, username, smtp, imap, accountType, deviceId) VALUES (2, 'crowds@gmail.com', 'bbtzgijwwyrtfhkg', 'Crowds', 'smtp.gmail.com', 'imap.gmail.com', '0', '1');

INSERT INTO audioemail.system_account (userId, address, password, username, smtp, imap, accountType, deviceId) VALUES (3, 'crowds@gmail.com', 'nbdnhxvaapxhkfql', 'Crowds', 'smtp.gmail.com', 'imap.gmail.com', '0', null);
















