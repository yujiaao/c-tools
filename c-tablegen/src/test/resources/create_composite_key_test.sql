-- 创建复合主键的测试表
CREATE TABLE IF NOT EXISTS composite_key_test (
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
