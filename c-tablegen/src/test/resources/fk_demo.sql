-- 创建 departments 表
CREATE TABLE departments (
    -- 部门 ID，主键，自增
                             id INT AUTO_INCREMENT PRIMARY KEY,
    -- 部门名称，不能为空
                             name VARCHAR(100) NOT NULL
);

-- 创建 employees 表
CREATE TABLE employees (
    -- 员工 ID，主键，自增
                           id INT AUTO_INCREMENT PRIMARY KEY,
    -- 员工姓名，不能为空
                           name VARCHAR(100) NOT NULL,
    -- 部门 ID，用于关联 departments 表的 id 字段
                           department_id INT,
    -- 外键约束，将 department_id 关联到 departments 表的 id 字段
    -- 当 departments 表中的记录被删除时，将员工表中对应的记录的 department_id 设置为 NULL
    -- 当 departments 表中的记录被更新时，同步更新员工表中对应的记录的 department_id
                           FOREIGN KEY (department_id) REFERENCES departments(id)
                               ON DELETE SET NULL
                               ON UPDATE CASCADE
);

-- 创建复合主键的测试表
CREATE TABLE composite_key_test (
    -- 第一个主键字段
    key1 VARCHAR(50) NOT NULL,
    -- 第二个主键字段
    key2 INT NOT NULL,
    -- 第三个主键字段
    key3 CHAR(10) NOT NULL,
    -- 普通字段
    description VARCHAR(200),
    -- 复合主键，按 key1, key2, key3 的顺序
    PRIMARY KEY (key1, key2, key3)
);

-- 插入测试数据
INSERT INTO composite_key_test (key1, key2, key3, description) VALUES 
('A', 1, 'X', '第一条记录'),
('B', 2, 'Y', '第二条记录'),
('C', 3, 'Z', '第三条记录');