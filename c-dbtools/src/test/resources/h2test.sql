CREATE SEQUENCE  IF NOT EXISTS SEQ_ID;

CREATE TABLE IF NOT EXISTS user1 (id int primary key,  name varchar(20) NOT NULL);

CREATE TABLE IF NOT EXISTS test (id int NOT NULL AUTO_INCREMENT COMMENT '这里是id!',
                        name varchar(100) DEFAULT NULL COMMENT '这里是名称！',
                        PRIMARY KEY (id)
) ;

CREATE TABLE IF NOT EXISTS T_LOG (
                         lid int  auto_increment primary key ,
                         uid int DEFAULT NULL,
                         uname varchar(32) DEFAULT NULL,
                         content text DEFAULT NULL,
                         ipaddress varchar(32) DEFAULT NULL,
                         url varchar(256) DEFAULT NULL,
                         op_time timestamp NOT NULL DEFAULT current_timestamp()

);
insert into T_LOG ( uid, content) values (1, 'OK');

CREATE TABLE IF NOT EXISTS t_sequence (
                              KEYNAME varchar(64) NOT NULL,
                              KEYVALUE bigint DEFAULT 100,
                              PRIMARY KEY (KEYNAME)
);

CREATE TABLE IF NOT EXISTS t_config (
                            c_key varchar(64) NOT NULL,
                            c_name varchar(64) NOT NULL DEFAULT '',
                            c_value varchar(1024) DEFAULT NULL,
                            PRIMARY KEY (c_key,c_name)
);
