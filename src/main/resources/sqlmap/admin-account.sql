-- ===============================================================
-- 1. 创建主管理员用户
-- ===============================================================

-- 插入主管理员用户
INSERT INTO users (id,
                   username,
                   email,
                   password_hash,
                   full_name,
                   created_at,
                   updated_at,
                   created_by)
VALUES (1, -- 用户ID
        'admin', -- 用户名
        'admin@beeyes.com', -- 邮箱
        '$2a$10$FUn4uhnCcQomvwuCtWUiAOGIqH2UCdIoUY24yRtMfzZYo/L.j3c6m', -- 密码: admin123 (BCrypt加密)
        '系统管理员', -- 全名
        CURRENT_TIMESTAMP, -- 创建时间
        CURRENT_TIMESTAMP, -- 更新时间
        1 -- 自己创建
       )
ON CONFLICT (id) DO NOTHING;

-- ===============================================================
-- 2. 分配管理员角色
-- ===============================================================

-- 插入管理员角色分配
-- INSERT INTO user_roles (id,
--                         user_id,
--                         role,
--                         granted_by,
--                         granted_at)
-- VALUES (1, -- 角色分配ID
--         1, -- 用户ID (admin)
--         1, -- 角色类型: 1 = 超级管理员 (应用层定义)
--         1, -- 授权者ID (admin自己)
--         CURRENT_TIMESTAMP -- 授权时间
--        )
-- ON CONFLICT (user_id, role) DO NOTHING;

-- -- ===============================================================
-- -- 3. 分配管理员权限
-- -- ===============================================================
--
-- -- 插入管理员权限分配 (超级管理员拥有所有权限)
-- INSERT INTO permissions (id,
--                          user_id,
--                          permission,
--                          granted_by,
--                          granted_at)
-- VALUES
-- -- 用户管理权限
-- (1, 1, 1, 1, CURRENT_TIMESTAMP),   -- 用户管理权限
-- (2, 1, 2, 1, CURRENT_TIMESTAMP),   -- 角色管理权限
-- (3, 1, 3, 1, CURRENT_TIMESTAMP),   -- 权限管理权限
--
-- -- 服务器管理权限
-- (4, 1, 4, 1, CURRENT_TIMESTAMP),   -- 服务器查看权限
-- (5, 1, 5, 1, CURRENT_TIMESTAMP),   -- 服务器管理权限
-- (6, 1, 6, 1, CURRENT_TIMESTAMP),   -- 服务器组管理权限
--
-- -- 告警管理权限
-- (7, 1, 7, 1, CURRENT_TIMESTAMP),   -- 告警规则管理权限
-- (8, 1, 8, 1, CURRENT_TIMESTAMP),   -- 告警通道管理权限
-- (9, 1, 9, 1, CURRENT_TIMESTAMP),   -- 告警事件管理权限
--
-- -- 容器管理权限
-- (10, 1, 10, 1, CURRENT_TIMESTAMP), -- 容器查看权限
-- (11, 1, 11, 1, CURRENT_TIMESTAMP), -- 容器管理权限
--
-- -- 编排管理权限
-- (12, 1, 12, 1, CURRENT_TIMESTAMP), -- 编排平台管理权限
-- (13, 1, 13, 1, CURRENT_TIMESTAMP)  -- 编排服务管理权限
-- ON CONFLICT (user_id, permission) DO NOTHING;
--
-- -- ===============================================================
-- -- 4. 创建默认服务器组
-- -- ===============================================================
--
-- -- 插入默认服务器组
-- INSERT INTO server_groups (id,
--                            name,
--                            description,
--                            created_by,
--                            created_at,
--                            updated_at)
-- VALUES (1, -- 服务器组ID
--         '默认组', -- 组名
--         '系统默认服务器组，包含所有未分组的服务器', -- 描述
--         1, -- 创建者ID (admin)
--         CURRENT_TIMESTAMP, -- 创建时间
--         CURRENT_TIMESTAMP -- 更新时间
--        )
-- ON CONFLICT (id) DO NOTHING;
--
-- -- ===============================================================
-- -- 5. 创建默认告警通道
-- -- ===============================================================
--
-- -- 插入默认邮件告警通道
-- INSERT INTO alert_channels (id,
--                             name,
--                             type,
--                             config,
--                             is_enabled,
--                             is_default,
--                             created_by,
--                             created_at,
--                             updated_at)
-- VALUES (1, -- 告警通道ID
--         '默认邮件通道', -- 通道名称
--         1, -- 通道类型: 1 = 邮件通道
--         '{
--           "smtp_host": "localhost",
--           "smtp_port": 587,
--           "smtp_username": "",
--           "smtp_password": "",
--           "from_email": "alert@beeyes.com",
--           "to_emails": [
--             "admin@beeyes.com"
--           ]
--         }', -- 配置
--         true, -- 启用状态
--         true, -- 默认通道
--         1, -- 创建者ID (admin)
--         CURRENT_TIMESTAMP, -- 创建时间
--         CURRENT_TIMESTAMP -- 更新时间
--        )
-- ON CONFLICT (id) DO NOTHING;
--
-- -- ===============================================================
-- -- 6. 记录审计日志
-- -- ===============================================================
--
-- -- 记录创建管理员账户的审计日志
-- INSERT INTO audit_logs (id,
--                         user_id,
--                         action,
--                         resource_type,
--                         resource_id,
--                         new_values,
--                         created_at)
-- VALUES (1, -- 审计记录ID
--         1, -- 操作用户ID (admin)
--         1, -- 操作类型: 1 = 创建
--         'user', -- 资源类型
--         1, -- 资源ID
--         '{
--           "username": "admin",
--           "email": "admin@beeyes.com",
--           "is_main_account": true
--         }', -- 新值
--         CURRENT_TIMESTAMP -- 操作时间
--        )
-- ON CONFLICT (id) DO NOTHING;
--
-- -- ===============================================================
-- -- 创建完成提示
-- -- ===============================================================
--
-- -- 查询创建的管理员信息
-- SELECT '管理员账户创建成功！' AS msg,
--        u.id                  AS user_id,
--        u.username,
--        u.email,
--        u.full_name,
--        u.is_active,
--        u.created_at
-- FROM users u
-- WHERE u.id = 1;
--
-- -- 显示登录信息
-- SELECT '请使用以下信息登录系统：' AS login_info,
--        '用户名: admin'           AS username,
--        '默认密码: admin123'      AS password,
--        '请登录后立即修改密码！'   AS security_reminder;
--
-- -- ===============================================================
-- -- 注意事项：
-- -- 1. 请在生产环境中修改默认密码
-- -- 2. 根据实际需求调整角色和权限的数字映射
-- -- 3. 根据实际环境配置邮件通道参数
-- -- 4. 建议定期备份 admin 账户
-- -- ===============================================================