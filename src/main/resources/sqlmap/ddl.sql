-- 用户表：存储系统用户基本信息和账户状态
create table if not exists users
(
    id              bigint       not null,                              -- 用户唯一标识
    parent_id       bigint,                                             -- 父账户ID（用于账户关联）
    username        varchar(50)  not null,                              -- 用户名（唯一）
    email           varchar(255) not null,                              -- 邮箱地址（唯一）
    password_hash   varchar(255) not null,                              -- 密码哈希值
    full_name       varchar(100),                                       -- 用户全名
    is_active       boolean                  default true,              -- 账户是否激活
    is_main_account boolean                  default false,             -- 是否为主账户
    created_at      timestamp with time zone default CURRENT_TIMESTAMP, -- 创建时间
    updated_at      timestamp with time zone default CURRENT_TIMESTAMP, -- 更新时间
    is_deleted      boolean      not null    default false,             -- 是否删除
    created_by      bigint,                                             -- 创建者ID
    phone           varchar(20),                                        -- 手机号

    primary key (id),
    unique (username),
    unique (email)
);

-- 用户表索引
create index if not exists idx_users_parent_id
    on users (parent_id);
-- 父账户查询索引

-- email和username已有唯一约束，无需额外索引

-- 创建复合索引：用户状态和创建时间查询优化
create index if not exists idx_users_active_created
    on users (is_active, created_at, created_by)
    where (is_deleted = false);

-- 创建复合索引：用户登录查询优化（按邮箱或用户名查找活跃用户）
create index if not exists idx_users_login_active
    on users (email, username, is_active)
    where (is_deleted = false and is_active = true);


-- 用户角色表：存储用户角色分配信息
create table if not exists user_roles
(
    id         bigint   not null,                                  -- 角色分配记录ID
    user_id    bigint   not null,                                  -- 用户ID
    role       smallint not null,                                  -- 角色类型（应用层维护枚举映射）
    granted_by bigint,                                             -- 授权者ID
    granted_at timestamp with time zone default CURRENT_TIMESTAMP, -- 授权时间
    expires_at timestamp with time zone,                           -- 角色过期时间（可为空表示永不过期）
    created_at timestamp with time zone default CURRENT_TIMESTAMP, -- 创建时间
    updated_at timestamp with time zone default CURRENT_TIMESTAMP, -- 更新时间
    is_deleted boolean  not null        default false,             -- 是否删除
    created_by bigint,                                             -- 创建者ID
    primary key (id),
    constraint unique_user_role
        unique (user_id, role)                                     -- 确保用户不会重复分配相同角色
);

-- 用户角色表索引

-- 创建复合索引：用户角色查询优化
create index if not exists idx_user_roles_user_active
    on user_roles (user_id, role, is_deleted)
    where (is_deleted = false);

-- 用户权限表：存储用户具体权限分配信息
create table if not exists permissions
(
    id         bigint   not null,                                  -- 权限分配记录ID
    user_id    bigint   not null,                                  -- 用户ID
    permission smallint not null,                                  -- 权限类型（应用层维护枚举映射）
    granted_by bigint,                                             -- 授权者ID
    granted_at timestamp with time zone default CURRENT_TIMESTAMP, -- 授权时间
    expires_at timestamp with time zone,                           -- 权限过期时间（可为空表示永不过期）
    created_at timestamp with time zone default CURRENT_TIMESTAMP, -- 创建时间
    updated_at timestamp with time zone default CURRENT_TIMESTAMP, -- 更新时间
    is_deleted boolean  not null        default false,             -- 是否删除
    created_by bigint,                                             -- 创建者ID
    primary key (id),
    constraint unique_user_permission
        unique (user_id, permission)                               -- 确保用户不会重复分配相同权限
);

-- 用户权限表索引

-- 创建复合索引：用户权限查询优化
create index if not exists idx_permissions_user_active
    on permissions (user_id, permission, is_deleted)
    where (is_deleted = false);

-- 服务器组表：存储服务器分组信息，支持层级结构
create table if not exists server_groups
(
    id              bigint       not null,                              -- 服务器组ID
    name            varchar(100) not null,                              -- 组名
    description     text,                                               -- 组描述
    parent_group_id bigint,                                             -- 父组ID（支持层级分组）
    created_at      timestamp with time zone default CURRENT_TIMESTAMP, -- 创建时间
    updated_at      timestamp with time zone default CURRENT_TIMESTAMP, -- 更新时间
    is_deleted      boolean      not null    default false,             -- 是否删除
    created_by      bigint,                                             -- 创建者ID
    primary key (id),
    constraint unique_group_name
        unique (name, parent_group_id)                                  -- 同一父组下组名唯一
);

-- 服务器组表索引
create index if not exists idx_server_groups_parent_id
    on server_groups (parent_group_id);
-- 父组查询索引（层级查询）

-- 创建复合索引：服务器组层级查询优化
create index if not exists idx_server_groups_hierarchy_active
    on server_groups (parent_group_id, is_deleted, created_at)
    where (is_deleted = false);

-- 服务器表：存储被监控服务器的详细信息
create table if not exists servers
(
    id          bigint       not null,                              -- 服务器唯一标识
    group_id    bigint,                                             -- 所属服务器组ID
    hostname    varchar(255) not null,                              -- 主机名
    ip_address  inet         not null,                              -- IP地址
    port        integer                  default 22,                -- SSH端口
    description text,                                               -- 服务器描述
    os_type     varchar(50),                                        -- 操作系统类型
    os_version  varchar(100),                                       -- 操作系统版本
    cpu_cores   integer,                                            -- CPU核心数
    memory_gb   integer,                                            -- 内存大小(GB)
    disk_gb     integer,                                            -- 磁盘大小(GB)
    status      smallint                 default 0,                 -- 服务器状态（应用层维护枚举映射）
    last_seen   timestamp with time zone,                           -- 最后在线时间
    created_at  timestamp with time zone default CURRENT_TIMESTAMP, -- 创建时间
    updated_at  timestamp with time zone default CURRENT_TIMESTAMP, -- 更新时间
    is_deleted  boolean      not null    default false,             -- 是否删除
    created_by  bigint,                                             -- 创建者ID
    api_key     varchar(255),
    primary key (id),
    constraint unique_server_hostname
        unique (hostname)                                           -- 主机名唯一
);

-- 服务器表索引
create index if not exists idx_servers_ip_address
    on servers (ip_address); -- IP地址查询索引

create index if not exists idx_servers_api_key
    on servers (api_key);
-- API密钥查询索引

-- 创建复合索引：服务器状态查询优化
create index if not exists idx_servers_status_active
    on servers (status, is_deleted, last_seen)
    where (is_deleted = false);

-- 创建复合索引：服务器组查询优化
create index if not exists idx_servers_group_active
    on servers (group_id, is_deleted, status)
    where (is_deleted = false);

-- SSH连接表：存储服务器SSH连接配置信息和认证方式
create table if not exists ssh_connections
(
    id                   bigint       not null,                              -- SSH连接配置ID
    server_id            bigint       not null,                              -- 关联服务器ID
    username             varchar(100) not null,                              -- SSH用户名
    auth_type            smallint     not null,                              -- 认证方式（应用层维护枚举映射）
    encrypted_credential varchar(300) not null,                              -- 加密存储的认证凭据（密码或密钥）
    port                 integer                  default 22,                -- SSH端口号
    connection_timeout   integer                  default 30,                -- 连接超时时间（秒）
    key_path             varchar(500),                                       -- 私钥文件路径（密钥认证时使用）
    is_active            boolean                  default true,              -- 连接配置是否启用
    last_test_success    boolean,                                            -- 最后一次连接测试是否成功
    last_test_at         timestamp with time zone,                           -- 最后一次连接测试时间
    created_at           timestamp with time zone default CURRENT_TIMESTAMP, -- 创建时间
    updated_at           timestamp with time zone default CURRENT_TIMESTAMP, -- 更新时间
    is_deleted           boolean      not null    default false,             -- 是否删除
    created_by           bigint,                                             -- 创建者ID
    primary key (id)
);

-- SSH连接表索引

-- 创建复合索引：SSH连接查询优化
create index if not exists idx_ssh_connections_server_active
    on ssh_connections (server_id, is_active, is_deleted)
    where (is_deleted = false);


-- 告警规则表：存储系统告警规则配置和触发条件
create table if not exists alert_rules
(
    id               bigint       not null,                              -- 告警规则ID
    name             varchar(200) not null,                              -- 规则名称
    description      text,                                               -- 规则描述
    severity         smallint     not null,                              -- 告警严重级别（应用层维护枚举映射）
    condition_type   smallint     not null,                              -- 条件类型（应用层维护枚举映射）
    condition_config jsonb        not null,                              -- 触发条件配置
    target_servers   jsonb,                                              -- 目标服务器列表
    target_groups    jsonb,                                              -- 目标服务器组列表
    is_enabled       boolean                  default true,              -- 规则是否启用
    cooldown_minutes integer                  default 5,                 -- 冷却时间（分钟）
    created_at       timestamp with time zone default CURRENT_TIMESTAMP, -- 创建时间
    updated_at       timestamp with time zone default CURRENT_TIMESTAMP, -- 更新时间
    is_deleted       boolean      not null    default false,             -- 是否删除
    created_by       bigint,                                             -- 创建者ID
    primary key (id)
);

-- 告警规则表索引

-- 创建复合索引：告警规则查询优化
create index if not exists idx_alert_rules_enabled_severity
    on alert_rules (is_enabled, severity, is_deleted)
    where (is_deleted = false);

-- 告警通道表：存储告警通知渠道配置（如邮件、短信、钉钉等）
create table if not exists alert_channels
(
    id         bigint       not null,                              -- 告警通道ID
    name       varchar(100) not null,                              -- 通道名称
    type       smallint     not null,                              -- 通道类型（应用层维护枚举映射）
    config     jsonb        not null,                              -- 通道配置信息
    is_enabled boolean                  default true,              -- 通道是否启用
    is_default boolean                  default false,             -- 是否为默认通道
    created_at timestamp with time zone default CURRENT_TIMESTAMP, -- 创建时间
    updated_at timestamp with time zone default CURRENT_TIMESTAMP, -- 更新时间
    is_deleted boolean      not null    default false,             -- 是否删除
    created_by bigint,                                             -- 创建者ID
    primary key (id)
);

-- 告警通道表索引

-- 创建复合索引：告警通道查询优化
create index if not exists idx_alert_channels_enabled_type
    on alert_channels (is_enabled, type, is_deleted)
    where (is_deleted = false);

-- 告警规则通道关联表：存储告警规则与通知通道的多对多关系
create table if not exists alert_rule_channels
(
    id         bigint  not null,                                   -- 关联记录ID
    rule_id    bigint  not null,                                   -- 告警规则ID
    channel_id bigint  not null,                                   -- 告警通道ID
    is_enabled boolean                  default true,              -- 关联是否启用
    created_at timestamp with time zone default CURRENT_TIMESTAMP, -- 创建时间
    updated_at timestamp with time zone default CURRENT_TIMESTAMP, -- 更新时间
    is_deleted boolean not null         default false,             -- 是否删除
    created_by bigint,                                             -- 创建者ID
    primary key (id),
    constraint unique_rule_channel
        unique (rule_id, channel_id)                               -- 规则与通道关联唯一
);

-- 告警规则通道关联表索引

-- 创建复合索引：告警规则通道关联查询优化
create index if not exists idx_alert_rule_channels_active
    on alert_rule_channels (rule_id, channel_id, is_enabled, is_deleted)
    where (is_deleted = false);

-- 告警事件表：存储告警触发后的具体事件记录和处理状态
create table if not exists alert_incidents
(
    id              bigint       not null,                              -- 告警事件ID
    rule_id         bigint       not null,                              -- 触发规则的ID
    server_id       bigint,                                             -- 关联服务器ID
    container_id    bigint,                                             -- 关联容器ID
    title           varchar(500) not null,                              -- 告警标题
    message         text         not null,                              -- 告警消息内容
    severity        smallint     not null,                              -- 告警严重级别（应用层维护枚举映射）
    status          smallint                 default 0,                 -- 事件处理状态（应用层维护枚举映射）
    trigger_data    jsonb,                                              -- 触发时的详细数据
    acknowledged_by bigint,                                             -- 确认人ID
    acknowledged_at timestamp with time zone,                           -- 确认时间
    resolved_by     bigint,                                             -- 解决人ID
    resolved_at     timestamp with time zone,                           -- 解决时间
    resolution_note text,                                               -- 解决说明
    created_at      timestamp with time zone default CURRENT_TIMESTAMP, -- 创建时间
    updated_at      timestamp with time zone default CURRENT_TIMESTAMP, -- 更新时间
    is_deleted      boolean      not null    default false,             -- 是否删除
    created_by      bigint,                                             -- 创建者ID
    primary key (id)
);

-- 告警事件表索引

-- 创建复合索引：告警事件状态查询优化
create index if not exists idx_alert_incidents_status_created
    on alert_incidents (status, severity, created_at)
    where (is_deleted = false);

-- 创建复合索引：服务器告警事件查询优化
create index if not exists idx_alert_incidents_server_created
    on alert_incidents (server_id, created_at, status)
    where (is_deleted = false);

-- 创建复合索引：告警事件时间序列查询优化（用于监控面板统计）
create index if not exists idx_alert_incidents_time_series
    on alert_incidents (created_at, severity, rule_id)
    where (is_deleted = false);

-- 创建复合索引：告警事件处理优化（按确认和解决时间）
create index if not exists idx_alert_incidents_resolution
    on alert_incidents (acknowledged_at, resolved_at, status)
    where (is_deleted = false);

-- 告警通知表：存储告警事件的通知发送记录和状态
create table if not exists alert_notifications
(
    id            bigint  not null,                                   -- 通知记录ID
    incident_id   bigint  not null,                                   -- 关联告警事件ID
    channel_id    bigint  not null,                                   -- 通知通道ID
    status        smallint                 default 0,                 -- 通知发送状态（应用层维护枚举映射）
    sent_at       timestamp with time zone,                           -- 发送时间
    error_message text,                                               -- 错误信息（发送失败时）
    retry_count   integer                  default 0,                 -- 重试次数
    created_at    timestamp with time zone default CURRENT_TIMESTAMP, -- 创建时间
    updated_at    timestamp with time zone default CURRENT_TIMESTAMP, -- 更新时间
    is_deleted    boolean not null         default false,             -- 是否删除
    created_by    bigint,                                             -- 创建者ID
    primary key (id)
);

-- 告警通知表索引

-- 创建复合索引：告警通知状态查询优化
create index if not exists idx_alert_notifications_status_created
    on alert_notifications (status, created_at, channel_id)
    where (is_deleted = false);

-- 创建复合索引：告警通知重试查询优化（用于重试失败的发送）
create index if not exists idx_alert_notifications_retry
    on alert_notifications (status, retry_count, created_at)
    where (is_deleted = false and status != 1);
-- 排除成功状态

-- 审计日志表：存储系统操作的审计记录和安全日志
create table if not exists audit_logs
(
    id            bigint      not null,                               -- 审计记录ID
    user_id       bigint,                                             -- 操作用户ID
    action        smallint    not null,                               -- 操作类型（应用层维护枚举映射）
    resource_type varchar(50) not null,                               -- 资源类型
    resource_id   bigint,                                             -- 资源ID
    old_values    jsonb,                                              -- 操作前的数据
    new_values    jsonb,                                              -- 操作后的数据
    ip_address    inet,                                               -- 客户端IP地址
    user_agent    text,                                               -- 客户端用户代理
    created_at    timestamp with time zone default CURRENT_TIMESTAMP, -- 操作时间
    updated_at    timestamp with time zone default CURRENT_TIMESTAMP, -- 更新时间
    is_deleted    boolean     not null     default false,             -- 是否删除
    created_by    bigint,                                             -- 创建者ID
    primary key (id)
);

-- 审计日志表索引

-- 创建复合索引：审计日志查询优化
create index if not exists idx_audit_logs_user_created
    on audit_logs (user_id, created_at, action)
    where (is_deleted = false);

-- 创建复合索引：资源操作审计查询优化
create index if not exists idx_audit_logs_resource_created
    on audit_logs (resource_type, resource_id, created_at)
    where (is_deleted = false);

-- 创建复合索引：审计日志安全监控查询优化（按IP和操作类型）
create index if not exists idx_audit_logs_security
    on audit_logs (ip_address, action, created_at)
    where (is_deleted = false);

-- 创建复合索引：审计日志数据清理优化（按创建时间）
create index if not exists idx_audit_logs_retention
    on audit_logs (created_at)
    where (is_deleted = false);

-- 定时任务表：存储系统定时任务的配置和执行状态
create table if not exists scheduled_tasks
(
    id              bigint       not null,                              -- 定时任务ID
    name            varchar(200) not null,                              -- 任务名称
    description     text,                                               -- 任务描述
    task_type       smallint     not null,                              -- 任务类型（应用层维护枚举映射）
    cron_expression varchar(100) not null,                              -- Cron表达式
    task_config     jsonb,                                              -- 任务配置参数
    is_enabled      boolean                  default true,              -- 任务是否启用
    max_retry_count integer                  default 3,                 -- 最大重试次数
    timeout_seconds integer                  default 300,               -- 超时时间（秒）
    last_run_at     timestamp with time zone,                           -- 最后执行时间
    next_run_at     timestamp with time zone,                           -- 下次执行时间
    last_run_status smallint,                                           -- 最后执行状态（应用层维护枚举映射）
    last_run_error  text,                                               -- 最后执行错误信息
    run_count       integer                  default 0,                 -- 执行次数
    success_count   integer                  default 0,                 -- 成功次数
    failure_count   integer                  default 0,                 -- 失败次数
    created_at      timestamp with time zone default CURRENT_TIMESTAMP, -- 创建时间
    updated_at      timestamp with time zone default CURRENT_TIMESTAMP, -- 更新时间
    is_deleted      boolean      not null    default false,             -- 是否删除
    created_by      bigint,                                             -- 创建者ID
    primary key (id),
    constraint unique_task_name
        unique (name)                                                   -- 任务名称唯一
);

-- 定时任务表索引

-- 创建复合索引：定时任务执行状态查询优化
create index if not exists idx_scheduled_tasks_enabled_status
    on scheduled_tasks (is_enabled, last_run_status, next_run_at)
    where (is_deleted = false);

-- 单独保留下次执行时间索引（任务调度器关键查询）
create index if not exists idx_scheduled_tasks_next_run
    on scheduled_tasks (next_run_at)
    where (is_enabled = true and is_deleted = false);

-- 表注释（使用 COMMENT ON TABLE 语句）
COMMENT ON TABLE users IS '用户表：存储系统用户基本信息和账户状态';
COMMENT ON TABLE user_roles IS '用户角色表：存储用户角色分配信息';
COMMENT ON TABLE permissions IS '用户权限表：存储用户具体权限分配信息';
COMMENT ON TABLE server_groups IS '服务器组表：存储服务器分组信息，支持层级结构';
COMMENT ON TABLE servers IS '服务器表：存储被监控服务器的详细信息';
COMMENT ON TABLE ssh_connections IS 'SSH连接表：存储服务器SSH连接配置信息和认证方式';
COMMENT ON TABLE alert_rules IS '告警规则表：存储系统告警规则配置和触发条件';
COMMENT ON TABLE alert_channels IS '告警通道表：存储告警通知渠道配置（如邮件、短信、钉钉等）';
COMMENT ON TABLE alert_rule_channels IS '告警规则通道关联表：存储告警规则与通知通道的多对多关系';
COMMENT ON TABLE alert_incidents IS '告警事件表：存储告警触发后的具体事件记录和处理状态';
COMMENT ON TABLE alert_notifications IS '告警通知表：存储告警事件的通知发送记录和状态';
COMMENT ON TABLE audit_logs IS '审计日志表：存储系统操作的审计记录和安全日志';
COMMENT ON TABLE scheduled_tasks IS '定时任务表：存储系统定时任务的配置和执行状态';

-- 字段注释（使用 COMMENT ON COLUMN 语句）

-- users 表字段注释
COMMENT ON COLUMN users.id IS '用户唯一标识';
COMMENT ON COLUMN users.parent_id IS '父账户ID（用于账户关联）';
COMMENT ON COLUMN users.username IS '用户名（唯一）';
COMMENT ON COLUMN users.email IS '邮箱地址（唯一）';
COMMENT ON COLUMN users.password_hash IS '密码哈希值';
COMMENT ON COLUMN users.full_name IS '用户全名';
COMMENT ON COLUMN users.is_active IS '账户是否激活';
COMMENT ON COLUMN users.is_main_account IS '是否为主账户';
COMMENT ON COLUMN users.created_at IS '创建时间';
COMMENT ON COLUMN users.updated_at IS '更新时间';
COMMENT ON COLUMN users.is_deleted IS '是否删除';
COMMENT ON COLUMN users.created_by IS '创建者ID';
COMMENT ON COLUMN users.phone IS '电话号码';

-- user_roles 表字段注释
COMMENT ON COLUMN user_roles.id IS '角色分配记录ID';
COMMENT ON COLUMN user_roles.user_id IS '用户ID';
COMMENT ON COLUMN user_roles.role IS '角色类型（应用层维护枚举映射）';
COMMENT ON COLUMN user_roles.granted_by IS '授权者ID';
COMMENT ON COLUMN user_roles.granted_at IS '授权时间';
COMMENT ON COLUMN user_roles.expires_at IS '角色过期时间（可为空表示永不过期）';
COMMENT ON COLUMN user_roles.created_at IS '创建时间';
COMMENT ON COLUMN user_roles.updated_at IS '更新时间';
COMMENT ON COLUMN user_roles.is_deleted IS '是否删除';
COMMENT ON COLUMN user_roles.created_by IS '创建者ID';

-- permissions 表字段注释
COMMENT ON COLUMN permissions.id IS '权限分配记录ID';
COMMENT ON COLUMN permissions.user_id IS '用户ID';
COMMENT ON COLUMN permissions.permission IS '权限类型（应用层维护枚举映射）';
COMMENT ON COLUMN permissions.granted_by IS '授权者ID';
COMMENT ON COLUMN permissions.granted_at IS '授权时间';
COMMENT ON COLUMN permissions.expires_at IS '权限过期时间（可为空表示永不过期）';
COMMENT ON COLUMN permissions.created_at IS '创建时间';
COMMENT ON COLUMN permissions.updated_at IS '更新时间';
COMMENT ON COLUMN permissions.is_deleted IS '是否删除';

-- server_groups 表字段注释
COMMENT ON COLUMN server_groups.id IS '服务器组ID';
COMMENT ON COLUMN server_groups.name IS '组名';
COMMENT ON COLUMN server_groups.description IS '组描述';
COMMENT ON COLUMN server_groups.parent_group_id IS '父组ID（支持层级分组）';
COMMENT ON COLUMN server_groups.created_by IS '创建者ID';
COMMENT ON COLUMN server_groups.created_at IS '创建时间';
COMMENT ON COLUMN server_groups.updated_at IS '更新时间';
COMMENT ON COLUMN server_groups.is_deleted IS '是否删除';

-- servers 表字段注释
COMMENT ON COLUMN servers.id IS '服务器唯一标识';
COMMENT ON COLUMN servers.group_id IS '所属服务器组ID';
COMMENT ON COLUMN servers.hostname IS '主机名';
COMMENT ON COLUMN servers.ip_address IS 'IP地址';
COMMENT ON COLUMN servers.port IS 'SSH端口';
COMMENT ON COLUMN servers.description IS '服务器描述';
COMMENT ON COLUMN servers.os_type IS '操作系统类型';
COMMENT ON COLUMN servers.os_version IS '操作系统版本';
COMMENT ON COLUMN servers.cpu_cores IS 'CPU核心数';
COMMENT ON COLUMN servers.memory_gb IS '内存大小(GB)';
COMMENT ON COLUMN servers.disk_gb IS '磁盘大小(GB)';
COMMENT ON COLUMN servers.status IS '服务器状态（应用层维护枚举映射）';
COMMENT ON COLUMN servers.last_seen IS '最后在线时间';
COMMENT ON COLUMN servers.created_by IS '创建者ID';
COMMENT ON COLUMN servers.created_at IS '创建时间';
COMMENT ON COLUMN servers.updated_at IS '更新时间';
COMMENT ON COLUMN servers.is_deleted IS '是否删除';

-- ssh_connections 表字段注释
COMMENT ON COLUMN ssh_connections.id IS 'SSH连接配置ID';
COMMENT ON COLUMN ssh_connections.server_id IS '关联服务器ID';
COMMENT ON COLUMN ssh_connections.username IS 'SSH用户名';
COMMENT ON COLUMN ssh_connections.auth_type IS '认证方式（应用层维护枚举映射）';
COMMENT ON COLUMN ssh_connections.encrypted_credential IS '加密存储的认证凭据（密码或密钥）';
COMMENT ON COLUMN ssh_connections.port IS 'SSH端口号';
COMMENT ON COLUMN ssh_connections.connection_timeout IS '连接超时时间（秒）';
COMMENT ON COLUMN ssh_connections.key_path IS '私钥文件路径（密钥认证时使用）';
COMMENT ON COLUMN ssh_connections.is_active IS '连接配置是否启用';
COMMENT ON COLUMN ssh_connections.last_test_success IS '最后一次连接测试是否成功';
COMMENT ON COLUMN ssh_connections.last_test_at IS '最后一次连接测试时间';
COMMENT ON COLUMN ssh_connections.created_by IS '创建者ID';
COMMENT ON COLUMN ssh_connections.created_at IS '创建时间';
COMMENT ON COLUMN ssh_connections.updated_at IS '更新时间';
COMMENT ON COLUMN ssh_connections.is_deleted IS '是否删除';

-- alert_rules 表字段注释
COMMENT ON COLUMN alert_rules.id IS '告警规则ID';
COMMENT ON COLUMN alert_rules.name IS '规则名称';
COMMENT ON COLUMN alert_rules.description IS '规则描述';
COMMENT ON COLUMN alert_rules.severity IS '告警严重级别（应用层维护枚举映射）';
COMMENT ON COLUMN alert_rules.condition_type IS '条件类型（应用层维护枚举映射）';
COMMENT ON COLUMN alert_rules.condition_config IS '触发条件配置';
COMMENT ON COLUMN alert_rules.target_servers IS '目标服务器列表';
COMMENT ON COLUMN alert_rules.target_groups IS '目标服务器组列表';
COMMENT ON COLUMN alert_rules.is_enabled IS '规则是否启用';
COMMENT ON COLUMN alert_rules.cooldown_minutes IS '冷却时间（分钟）';
COMMENT ON COLUMN alert_rules.created_by IS '创建者ID';
COMMENT ON COLUMN alert_rules.created_at IS '创建时间';
COMMENT ON COLUMN alert_rules.updated_at IS '更新时间';
COMMENT ON COLUMN alert_rules.is_deleted IS '是否删除';

-- alert_channels 表字段注释
COMMENT ON COLUMN alert_channels.id IS '告警通道ID';
COMMENT ON COLUMN alert_channels.name IS '通道名称';
COMMENT ON COLUMN alert_channels.type IS '通道类型（应用层维护枚举映射）';
COMMENT ON COLUMN alert_channels.config IS '通道配置信息';
COMMENT ON COLUMN alert_channels.is_enabled IS '通道是否启用';
COMMENT ON COLUMN alert_channels.is_default IS '是否为默认通道';
COMMENT ON COLUMN alert_channels.created_by IS '创建者ID';
COMMENT ON COLUMN alert_channels.created_at IS '创建时间';
COMMENT ON COLUMN alert_channels.updated_at IS '更新时间';
COMMENT ON COLUMN alert_channels.is_deleted IS '是否删除';

-- alert_rule_channels 表字段注释
COMMENT ON COLUMN alert_rule_channels.id IS '关联记录ID';
COMMENT ON COLUMN alert_rule_channels.rule_id IS '告警规则ID';
COMMENT ON COLUMN alert_rule_channels.channel_id IS '告警通道ID';
COMMENT ON COLUMN alert_rule_channels.is_enabled IS '关联是否启用';
COMMENT ON COLUMN alert_rule_channels.created_at IS '创建时间';
COMMENT ON COLUMN alert_rule_channels.updated_at IS '更新时间';
COMMENT ON COLUMN alert_rule_channels.is_deleted IS '是否删除';

-- alert_incidents 表字段注释
COMMENT ON COLUMN alert_incidents.id IS '告警事件ID';
COMMENT ON COLUMN alert_incidents.rule_id IS '触发规则的ID';
COMMENT ON COLUMN alert_incidents.server_id IS '关联服务器ID';
COMMENT ON COLUMN alert_incidents.container_id IS '关联容器ID';
COMMENT ON COLUMN alert_incidents.title IS '告警标题';
COMMENT ON COLUMN alert_incidents.message IS '告警消息内容';
COMMENT ON COLUMN alert_incidents.severity IS '告警严重级别（应用层维护枚举映射）';
COMMENT ON COLUMN alert_incidents.status IS '事件处理状态（应用层维护枚举映射）';
COMMENT ON COLUMN alert_incidents.trigger_data IS '触发时的详细数据';
COMMENT ON COLUMN alert_incidents.acknowledged_by IS '确认人ID';
COMMENT ON COLUMN alert_incidents.acknowledged_at IS '确认时间';
COMMENT ON COLUMN alert_incidents.resolved_by IS '解决人ID';
COMMENT ON COLUMN alert_incidents.resolved_at IS '解决时间';
COMMENT ON COLUMN alert_incidents.resolution_note IS '解决说明';
COMMENT ON COLUMN alert_incidents.created_at IS '创建时间';
COMMENT ON COLUMN alert_incidents.updated_at IS '更新时间';
COMMENT ON COLUMN alert_incidents.is_deleted IS '是否删除';

-- alert_notifications 表字段注释
COMMENT ON COLUMN alert_notifications.id IS '通知记录ID';
COMMENT ON COLUMN alert_notifications.incident_id IS '关联告警事件ID';
COMMENT ON COLUMN alert_notifications.channel_id IS '通知通道ID';
COMMENT ON COLUMN alert_notifications.status IS '通知发送状态（应用层维护枚举映射）';
COMMENT ON COLUMN alert_notifications.sent_at IS '发送时间';
COMMENT ON COLUMN alert_notifications.error_message IS '错误信息（发送失败时）';
COMMENT ON COLUMN alert_notifications.retry_count IS '重试次数';
COMMENT ON COLUMN alert_notifications.created_at IS '创建时间';
COMMENT ON COLUMN alert_notifications.updated_at IS '更新时间';
COMMENT ON COLUMN alert_notifications.is_deleted IS '是否删除';

-- audit_logs 表字段注释
COMMENT ON COLUMN audit_logs.id IS '审计记录ID';
COMMENT ON COLUMN audit_logs.user_id IS '操作用户ID';
COMMENT ON COLUMN audit_logs.action IS '操作类型（应用层维护枚举映射）';
COMMENT ON COLUMN audit_logs.resource_type IS '资源类型';
COMMENT ON COLUMN audit_logs.resource_id IS '资源ID';
COMMENT ON COLUMN audit_logs.old_values IS '操作前的数据';
COMMENT ON COLUMN audit_logs.new_values IS '操作后的数据';
COMMENT ON COLUMN audit_logs.ip_address IS '客户端IP地址';
COMMENT ON COLUMN audit_logs.user_agent IS '客户端用户代理';
COMMENT ON COLUMN audit_logs.created_at IS '操作时间';
COMMENT ON COLUMN audit_logs.updated_at IS '更新时间';
COMMENT ON COLUMN audit_logs.is_deleted IS '是否删除';

-- scheduled_tasks 表字段注释
COMMENT ON COLUMN scheduled_tasks.id IS '定时任务ID';
COMMENT ON COLUMN scheduled_tasks.name IS '任务名称';
COMMENT ON COLUMN scheduled_tasks.description IS '任务描述';
COMMENT ON COLUMN scheduled_tasks.task_type IS '任务类型（应用层维护枚举映射）';
COMMENT ON COLUMN scheduled_tasks.cron_expression IS 'Cron表达式';
COMMENT ON COLUMN scheduled_tasks.task_config IS '任务配置参数';
COMMENT ON COLUMN scheduled_tasks.is_enabled IS '任务是否启用';
COMMENT ON COLUMN scheduled_tasks.max_retry_count IS '最大重试次数';
COMMENT ON COLUMN scheduled_tasks.timeout_seconds IS '超时时间（秒）';
COMMENT ON COLUMN scheduled_tasks.last_run_at IS '最后执行时间';
COMMENT ON COLUMN scheduled_tasks.next_run_at IS '下次执行时间';
COMMENT ON COLUMN scheduled_tasks.last_run_status IS '最后执行状态（应用层维护枚举映射）';
COMMENT ON COLUMN scheduled_tasks.last_run_error IS '最后执行错误信息';
COMMENT ON COLUMN scheduled_tasks.run_count IS '执行次数';
COMMENT ON COLUMN scheduled_tasks.success_count IS '成功次数';
COMMENT ON COLUMN scheduled_tasks.failure_count IS '失败次数';
COMMENT ON COLUMN scheduled_tasks.created_by IS '创建者ID';
COMMENT ON COLUMN scheduled_tasks.created_at IS '创建时间';
COMMENT ON COLUMN scheduled_tasks.updated_at IS '更新时间';
COMMENT ON COLUMN scheduled_tasks.is_deleted IS '是否删除';
