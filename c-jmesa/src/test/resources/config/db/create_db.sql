create table t_main
(
    id        int auto_increment,
    user_name varchar(32),
    gender    int,
    edu       int
);

create table education
(
    id       int auto_increment,
    edu_name varchar(32)
);

create table gender
(
    id  int auto_increment,
    sex varchar(32)
);

insert into education (edu_name)
values ('小学'),
       ('中学'),
       ('大学');
insert into gender(sex)
values ('男'),
       ('女'),
       ('变态');

insert into t_main(user_name, gender, edu)
VALUES ('张三', 1, 1),
       ('李四', 2, 2),
       ('王五', 3, 3);

create table customer (
    id int primary key,
    firstname varchar(32),
    age int,
    lastname varchar(32),
    salary int
);

INSERT INTO customer VALUES (1, 'Ramesh', 32, 'Ahmedabad', 2000);
INSERT INTO customer VALUES (2, 'Khilan', 25, 'Delhi', 1500);
INSERT INTO customer VALUES (3, 'kaushik', 23, 'Kota', 2000);
INSERT INTO customer VALUES (4, 'Chaitali', 25, 'Mumbai', 6500);
INSERT INTO customer VALUES (5, 'Hardik', 27, 'Bhopal', 8500);
INSERT INTO customer VALUES (6, 'Komal', 22, 'MP', 4500);
INSERT INTO customer VALUES (7, 'Muffy', 24, 'Indore', 10000);

CREATE TABLE t_config (
       c_key varchar(64) NOT NULL,
       c_name varchar(64) NOT NULL DEFAULT '',
       c_value varchar(1024) DEFAULT NULL,
       PRIMARY KEY (c_key,c_name)
     );

insert into t_config values ( 'key', 'name','value');
insert into t_config values ( 'key1', 'name1','value1');
insert into t_config values('key2','name2', 'value2');
