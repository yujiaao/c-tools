-- 创建测试表
DROP TABLE IF EXISTS client_user;
CREATE TABLE client_user (
    user_id VARCHAR(64) NOT NULL PRIMARY KEY,
    nick_name VARCHAR(32),
    phone_number VARCHAR(16),
    gender TINYINT DEFAULT 0
);
