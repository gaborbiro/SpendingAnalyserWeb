use ofxanalyser;

drop table TRANSACTION;
drop table USER_DOCUMENT;
drop table APP_USER;

create table APP_USER (
   id BIGINT NOT NULL AUTO_INCREMENT,
   first_name VARCHAR(30) NOT NULL,
   last_name VARCHAR(30) NOT NULL,
   email VARCHAR(30) NOT NULL,
   PRIMARY KEY (id),
   UNIQUE (email)
);

create table USER_DOCUMENT (
   id BIGINT NOT NULL AUTO_INCREMENT,
   user_id BIGINT NOT NULL,
   name VARCHAR(100) NOT NULL,
   description VARCHAR(255),
   content_type VARCHAR(100) NOT NULL,
   start_date BIGINT NOT NULL,
   end_date BIGINT NOT NULL,
   PRIMARY KEY (id),
   CONSTRAINT document_user FOREIGN KEY (user_id) REFERENCES APP_USER (id) ON UPDATE CASCADE ON DELETE CASCADE
);

create table TRANSACTION (
   id BIGINT NOT NULL AUTO_INCREMENT,
   user_id BIGINT NOT NULL,
   document_id BIGINT NOT NULL,
   description VARCHAR(255) NOT NULL,
   date_ DATE NOT NULL,
   amount BIGINT NOT NULL,
   category VARCHAR(255),
   is_subscription TINYINT(1),
   PRIMARY KEY (id),
   CONSTRAINT transaction_user FOREIGN KEY (user_id) REFERENCES APP_USER (id) ON UPDATE CASCADE ON DELETE CASCADE,
   CONSTRAINT transaction_document FOREIGN KEY (document_id) REFERENCES USER_DOCUMENT (id) ON UPDATE CASCADE ON DELETE CASCADE
);

ALTER TABLE transaction
ADD UNIQUE INDEX no_duplicates (description, date_, amount);
