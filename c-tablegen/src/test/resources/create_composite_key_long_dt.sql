create if not exists table test_gen_long_dt_key (
    id bigint AUTO_INCREMENT not null,
    dt datetime,
    name varchar(100),
    age int,
    birth datetime,
    edu_id int,
    gender varchar(10),
    sex boolean,
    child boolean,
    primary key (id, dt)
);
insert into test_gen_long_dt_key (id, dt, name, age, birth, edu_id, gender, sex, child) values (1, '2021-01-01', '张三', 18, '2021-01-01', 1, '男', true, true);
insert into test_gen_long_dt_key (id, dt, name, age, birth, edu_id, gender, sex, child) values (2, '2021-01-02', '李四', 20, '2021-01-02', 2, '女', false, false);
insert into test_gen_long_dt_key (id, dt, name, age, birth, edu_id, gender, sex, child) values (3, '2021-01-03', '王五', 22, '2021-01-03', 3, '男', true, false);
insert into test_gen_long_dt_key (id, dt, name, age, birth, edu_id, gender, sex, child) values (4, '2021-01-04', '赵六', 24, '2021-01-04', 4, '女', false, true);
insert into test_gen_long_dt_key (id, dt, name, age, birth, edu_id, gender, sex, child) values (5, '2021-01-05', '孙七', 26, '2021-01-05', 5, '男', true, false);
insert into test_gen_long_dt_key (id, dt, name, age, birth, edu_id, gender, sex, child) values (6, '2021-01-06', '周八', 28, '2021-01-06', 6, '女', false, true);
insert into test_gen_long_dt_key (id, dt, name, age, birth, edu_id, gender, sex, child) values (7, '2021-01-07', '吴九', 30, '2021-01-07', 7, '男', true, false);
insert into test_gen_long_dt_key (id, dt, name, age, birth, edu_id, gender, sex, child) values (8, '2021-01-08', '郑十', 32, '2021-01-08', 8, '女', false, true);
