-- 超级管理员角色
INSERT IGNORE INTO yanwu_role (id, role_name, description) VALUES (1, '超级管理员', '超级管理员角色');
-- 超级管理员用户
INSERT IGNORE INTO yanwu_user (id, account, password, user_name, sex, role_id, description) VALUES (1, 'admin', '123456', '超级管理员', 1, 1, '超级管理员');