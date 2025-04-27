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
    -- 当 departments 表中的记录被更新时，同步更新员工表中对应的 department_id
                           FOREIGN KEY (department_id) REFERENCES departments(id)
                               ON DELETE SET NULL
                               ON UPDATE CASCADE
);