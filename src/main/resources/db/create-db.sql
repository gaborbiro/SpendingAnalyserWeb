use ofxanalyser;

drop table USER_DOCUMENT;
drop table APP_USER;

create table APP_USER (
   id BIGINT NOT NULL AUTO_INCREMENT,
   sso_id VARCHAR(30) NOT NULL,
   first_name VARCHAR(30) NOT NULL,
   last_name  VARCHAR(30) NOT NULL,
   email VARCHAR(30) NOT NULL,
   PRIMARY KEY (id),
   UNIQUE (email)
);

create table USER_DOCUMENT (
   id BIGINT NOT NULL AUTO_INCREMENT,
   user_id BIGINT NOT NULL,
   name  VARCHAR(100) NOT NULL,
   description VARCHAR(255) ,
   contentType VARCHAR(100) NOT NULL,
   content longblob NOT NULL,
   PRIMARY KEY (id),
   CONSTRAINT document_user FOREIGN KEY (user_id) REFERENCES APP_USER (id) ON UPDATE CASCADE ON DELETE CASCADE
);