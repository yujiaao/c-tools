CREATE TABLE IF NOT EXISTS t_edu (id int(11) NOT NULL AUTO_INCREMENT COMMENT '这里是id!',
                                  degree varchar(100) DEFAULT NULL COMMENT '这里是名称！',
                                  PRIMARY KEY (id)
) ENGINE = InnoDB comment '教育程度';


CREATE TABLE IF NOT EXISTS test_gen (id int(11) NOT NULL AUTO_INCREMENT COMMENT '这里是id!',
                                 name varchar(100) DEFAULT NULL COMMENT '这里是名称！',
                                 age smallint DEFAULT NULL COMMENT '这里是年龄',
                                     birth datetime DEFAULT NULL COMMENT '这里是日期！',
                                     edu_id int DEFAULT NULL COMMENT '教育程度',
                                PRIMARY KEY (id),
                                KEY  t_edu_id(edu_id),
                                CONSTRAINT  key_edu_id_fk  FOREIGN  KEY  (edu_id)  REFERENCES  t_edu (id)
) ENGINE = InnoDB comment '测试表';



--  CREATE <索引名> ON <表名> (<列名> [<长度>] [ ASC | DESC])
create index ind_test_gen on test_gen (edu_id desc, name);
