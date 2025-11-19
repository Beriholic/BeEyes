create table if not exists users
(
    id              bigint                                 not null,
    parent_id       bigint,
    username        varchar(50)                            not null,
    email           varchar(255)                           not null,
    password_hash   varchar(255)                           not null,
    full_name       varchar(100),
    is_active       boolean                  default true,
    is_main_account boolean                  default false,
    created_at      timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at      timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted      boolean                  default false not null,
    created_by      bigint,
    phone           varchar(20)
);

comment on table users is '用户表：存储系统用户基本信息和账户状态';

comment on column users.id is '用户唯一标识';

comment on column users.parent_id is '父账户ID（用于账户关联）';

comment on column users.username is '用户名（唯一）';

comment on column users.email is '邮箱地址（唯一）';

comment on column users.password_hash is '密码哈希值';

comment on column users.full_name is '用户全名';

comment on column users.is_active is '账户是否激活';

comment on column users.is_main_account is '是否为主账户';

comment on column users.created_at is '创建时间';

comment on column users.updated_at is '更新时间';

comment on column users.is_deleted is '是否删除';

comment on column users.created_by is '创建者ID';

comment on column users.phone is '电话号码';

alter table users
    owner to be_eyes;

create index if not exists idx_users_parent_id
    on users (parent_id);

create index if not exists idx_users_active_created
    on users (is_active, created_at, created_by)
    where (is_deleted = false);

create index if not exists idx_users_login_active
    on users (email, username, is_active)
    where ((is_deleted = false) AND (is_active = true));

alter table users
    add primary key (id);

alter table users
    add unique (username);

alter table users
    add unique (email);

create table if not exists user_roles
(
    id         bigint                                 not null,
    user_id    bigint                                 not null,
    role       smallint                               not null,
    granted_by bigint,
    granted_at timestamp with time zone default CURRENT_TIMESTAMP,
    expires_at timestamp with time zone,
    created_at timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted boolean                  default false not null,
    created_by bigint
);

comment on table user_roles is '用户角色表：存储用户角色分配信息';

comment on column user_roles.id is '角色分配记录ID';

comment on column user_roles.user_id is '用户ID';

comment on column user_roles.role is '角色类型（应用层维护枚举映射）';

comment on column user_roles.granted_by is '授权者ID';

comment on column user_roles.granted_at is '授权时间';

comment on column user_roles.expires_at is '角色过期时间（可为空表示永不过期）';

comment on column user_roles.created_at is '创建时间';

comment on column user_roles.updated_at is '更新时间';

comment on column user_roles.is_deleted is '是否删除';

comment on column user_roles.created_by is '创建者ID';

alter table user_roles
    owner to be_eyes;

create index if not exists idx_user_roles_user_active
    on user_roles (user_id, role, is_deleted)
    where (is_deleted = false);

alter table user_roles
    add primary key (id);

alter table user_roles
    add constraint unique_user_role
        unique (user_id, role);

create table if not exists permissions
(
    id         bigint                                 not null,
    user_id    bigint                                 not null,
    permission smallint                               not null,
    granted_by bigint,
    granted_at timestamp with time zone default CURRENT_TIMESTAMP,
    expires_at timestamp with time zone,
    created_at timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted boolean                  default false not null,
    created_by bigint
);

comment on table permissions is '用户权限表：存储用户具体权限分配信息';

comment on column permissions.id is '权限分配记录ID';

comment on column permissions.user_id is '用户ID';

comment on column permissions.permission is '权限类型（应用层维护枚举映射）';

comment on column permissions.granted_by is '授权者ID';

comment on column permissions.granted_at is '授权时间';

comment on column permissions.expires_at is '权限过期时间（可为空表示永不过期）';

comment on column permissions.created_at is '创建时间';

comment on column permissions.updated_at is '更新时间';

comment on column permissions.is_deleted is '是否删除';

alter table permissions
    owner to be_eyes;

create index if not exists idx_permissions_user_active
    on permissions (user_id, permission, is_deleted)
    where (is_deleted = false);

alter table permissions
    add primary key (id);

alter table permissions
    add constraint unique_user_permission
        unique (user_id, permission);

create table if not exists server_groups
(
    id              bigint                                 not null,
    name            varchar(100)                           not null,
    description     text,
    parent_group_id bigint,
    created_at      timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at      timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted      boolean                  default false not null,
    created_by      bigint
);

comment on table server_groups is '服务器组表：存储服务器分组信息，支持层级结构';

comment on column server_groups.id is '服务器组ID';

comment on column server_groups.name is '组名';

comment on column server_groups.description is '组描述';

comment on column server_groups.parent_group_id is '父组ID（支持层级分组）';

comment on column server_groups.created_at is '创建时间';

comment on column server_groups.updated_at is '更新时间';

comment on column server_groups.is_deleted is '是否删除';

comment on column server_groups.created_by is '创建者ID';

alter table server_groups
    owner to be_eyes;

create index if not exists idx_server_groups_parent_id
    on server_groups (parent_group_id);

create index if not exists idx_server_groups_hierarchy_active
    on server_groups (parent_group_id, is_deleted, created_at)
    where (is_deleted = false);

alter table server_groups
    add primary key (id);

alter table server_groups
    add constraint unique_group_name
        unique (name, parent_group_id);

create table if not exists ssh_connections
(
    id                   bigint                                 not null,
    server_id            bigint                                 not null,
    username             varchar(100)                           not null,
    auth_type            smallint                               not null,
    encrypted_credential varchar(300)                           not null,
    port                 integer                  default 22,
    connection_timeout   integer                  default 30,
    key_path             varchar(500),
    is_active            boolean                  default true,
    last_test_success    boolean,
    last_test_at         timestamp with time zone,
    created_at           timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at           timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted           boolean                  default false not null,
    created_by           bigint
);

comment on table ssh_connections is 'SSH连接表：存储服务器SSH连接配置信息和认证方式';

comment on column ssh_connections.id is 'SSH连接配置ID';

comment on column ssh_connections.server_id is '关联服务器ID';

comment on column ssh_connections.username is 'SSH用户名';

comment on column ssh_connections.auth_type is '认证方式（应用层维护枚举映射）';

comment on column ssh_connections.encrypted_credential is '加密存储的认证凭据（密码或密钥）';

comment on column ssh_connections.port is 'SSH端口号';

comment on column ssh_connections.connection_timeout is '连接超时时间（秒）';

comment on column ssh_connections.key_path is '私钥文件路径（密钥认证时使用）';

comment on column ssh_connections.is_active is '连接配置是否启用';

comment on column ssh_connections.last_test_success is '最后一次连接测试是否成功';

comment on column ssh_connections.last_test_at is '最后一次连接测试时间';

comment on column ssh_connections.created_at is '创建时间';

comment on column ssh_connections.updated_at is '更新时间';

comment on column ssh_connections.is_deleted is '是否删除';

comment on column ssh_connections.created_by is '创建者ID';

alter table ssh_connections
    owner to be_eyes;

create index if not exists idx_ssh_connections_server_active
    on ssh_connections (server_id, is_active, is_deleted)
    where (is_deleted = false);

alter table ssh_connections
    add primary key (id);

create table if not exists alert_rules
(
    id               bigint                                 not null,
    name             varchar(200)                           not null,
    description      text,
    severity         smallint                               not null,
    condition_type   smallint                               not null,
    condition_config jsonb                                  not null,
    target_servers   jsonb,
    target_groups    jsonb,
    is_enabled       boolean                  default true,
    cooldown_minutes integer                  default 5,
    created_at       timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at       timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted       boolean                  default false not null,
    created_by       bigint
);

comment on table alert_rules is '告警规则表：存储系统告警规则配置和触发条件';

comment on column alert_rules.id is '告警规则ID';

comment on column alert_rules.name is '规则名称';

comment on column alert_rules.description is '规则描述';

comment on column alert_rules.severity is '告警严重级别（应用层维护枚举映射）';

comment on column alert_rules.condition_type is '条件类型（应用层维护枚举映射）';

comment on column alert_rules.condition_config is '触发条件配置';

comment on column alert_rules.target_servers is '目标服务器列表';

comment on column alert_rules.target_groups is '目标服务器组列表';

comment on column alert_rules.is_enabled is '规则是否启用';

comment on column alert_rules.cooldown_minutes is '冷却时间（分钟）';

comment on column alert_rules.created_at is '创建时间';

comment on column alert_rules.updated_at is '更新时间';

comment on column alert_rules.is_deleted is '是否删除';

comment on column alert_rules.created_by is '创建者ID';

alter table alert_rules
    owner to be_eyes;

create index if not exists idx_alert_rules_enabled_severity
    on alert_rules (is_enabled, severity, is_deleted)
    where (is_deleted = false);

alter table alert_rules
    add primary key (id);

create table if not exists alert_channels
(
    id         bigint                                 not null,
    name       varchar(100)                           not null,
    type       smallint                               not null,
    config     jsonb                                  not null,
    is_enabled boolean                  default true,
    is_default boolean                  default false,
    created_at timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted boolean                  default false not null,
    created_by bigint
);

comment on table alert_channels is '告警通道表：存储告警通知渠道配置（如邮件、短信、钉钉等）';

comment on column alert_channels.id is '告警通道ID';

comment on column alert_channels.name is '通道名称';

comment on column alert_channels.type is '通道类型（应用层维护枚举映射）';

comment on column alert_channels.config is '通道配置信息';

comment on column alert_channels.is_enabled is '通道是否启用';

comment on column alert_channels.is_default is '是否为默认通道';

comment on column alert_channels.created_at is '创建时间';

comment on column alert_channels.updated_at is '更新时间';

comment on column alert_channels.is_deleted is '是否删除';

comment on column alert_channels.created_by is '创建者ID';

alter table alert_channels
    owner to be_eyes;

create index if not exists idx_alert_channels_enabled_type
    on alert_channels (is_enabled, type, is_deleted)
    where (is_deleted = false);

alter table alert_channels
    add primary key (id);

create table if not exists alert_rule_channels
(
    id         bigint                                 not null,
    rule_id    bigint                                 not null,
    channel_id bigint                                 not null,
    is_enabled boolean                  default true,
    created_at timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted boolean                  default false not null,
    created_by bigint
);

comment on table alert_rule_channels is '告警规则通道关联表：存储告警规则与通知通道的多对多关系';

comment on column alert_rule_channels.id is '关联记录ID';

comment on column alert_rule_channels.rule_id is '告警规则ID';

comment on column alert_rule_channels.channel_id is '告警通道ID';

comment on column alert_rule_channels.is_enabled is '关联是否启用';

comment on column alert_rule_channels.created_at is '创建时间';

comment on column alert_rule_channels.updated_at is '更新时间';

comment on column alert_rule_channels.is_deleted is '是否删除';

alter table alert_rule_channels
    owner to be_eyes;

create index if not exists idx_alert_rule_channels_active
    on alert_rule_channels (rule_id, channel_id, is_enabled, is_deleted)
    where (is_deleted = false);

alter table alert_rule_channels
    add primary key (id);

alter table alert_rule_channels
    add constraint unique_rule_channel
        unique (rule_id, channel_id);

create table if not exists alert_incidents
(
    id              bigint                                 not null,
    rule_id         bigint                                 not null,
    server_id       bigint,
    container_id    bigint,
    title           varchar(500)                           not null,
    message         text                                   not null,
    severity        smallint                               not null,
    status          smallint                 default 0,
    trigger_data    jsonb,
    acknowledged_by bigint,
    acknowledged_at timestamp with time zone,
    resolved_by     bigint,
    resolved_at     timestamp with time zone,
    resolution_note text,
    created_at      timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at      timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted      boolean                  default false not null,
    created_by      bigint
);

comment on table alert_incidents is '告警事件表：存储告警触发后的具体事件记录和处理状态';

comment on column alert_incidents.id is '告警事件ID';

comment on column alert_incidents.rule_id is '触发规则的ID';

comment on column alert_incidents.server_id is '关联服务器ID';

comment on column alert_incidents.container_id is '关联容器ID';

comment on column alert_incidents.title is '告警标题';

comment on column alert_incidents.message is '告警消息内容';

comment on column alert_incidents.severity is '告警严重级别（应用层维护枚举映射）';

comment on column alert_incidents.status is '事件处理状态（应用层维护枚举映射）';

comment on column alert_incidents.trigger_data is '触发时的详细数据';

comment on column alert_incidents.acknowledged_by is '确认人ID';

comment on column alert_incidents.acknowledged_at is '确认时间';

comment on column alert_incidents.resolved_by is '解决人ID';

comment on column alert_incidents.resolved_at is '解决时间';

comment on column alert_incidents.resolution_note is '解决说明';

comment on column alert_incidents.created_at is '创建时间';

comment on column alert_incidents.updated_at is '更新时间';

comment on column alert_incidents.is_deleted is '是否删除';

alter table alert_incidents
    owner to be_eyes;

create index if not exists idx_alert_incidents_status_created
    on alert_incidents (status, severity, created_at)
    where (is_deleted = false);

create index if not exists idx_alert_incidents_server_created
    on alert_incidents (server_id, created_at, status)
    where (is_deleted = false);

create index if not exists idx_alert_incidents_time_series
    on alert_incidents (created_at, severity, rule_id)
    where (is_deleted = false);

create index if not exists idx_alert_incidents_resolution
    on alert_incidents (acknowledged_at, resolved_at, status)
    where (is_deleted = false);

alter table alert_incidents
    add primary key (id);

create table if not exists alert_notifications
(
    id            bigint                                 not null,
    incident_id   bigint                                 not null,
    channel_id    bigint                                 not null,
    status        smallint                 default 0,
    sent_at       timestamp with time zone,
    error_message text,
    retry_count   integer                  default 0,
    created_at    timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at    timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted    boolean                  default false not null,
    created_by    bigint
);

comment on table alert_notifications is '告警通知表：存储告警事件的通知发送记录和状态';

comment on column alert_notifications.id is '通知记录ID';

comment on column alert_notifications.incident_id is '关联告警事件ID';

comment on column alert_notifications.channel_id is '通知通道ID';

comment on column alert_notifications.status is '通知发送状态（应用层维护枚举映射）';

comment on column alert_notifications.sent_at is '发送时间';

comment on column alert_notifications.error_message is '错误信息（发送失败时）';

comment on column alert_notifications.retry_count is '重试次数';

comment on column alert_notifications.created_at is '创建时间';

comment on column alert_notifications.updated_at is '更新时间';

comment on column alert_notifications.is_deleted is '是否删除';

alter table alert_notifications
    owner to be_eyes;

create index if not exists idx_alert_notifications_status_created
    on alert_notifications (status, created_at, channel_id)
    where (is_deleted = false);

create index if not exists idx_alert_notifications_retry
    on alert_notifications (status, retry_count, created_at)
    where ((is_deleted = false) AND (status <> 1));

alter table alert_notifications
    add primary key (id);

create table if not exists audit_logs
(
    id            bigint                                 not null,
    user_id       bigint,
    action        smallint                               not null,
    resource_type varchar(50)                            not null,
    resource_id   bigint,
    old_values    jsonb,
    new_values    jsonb,
    ip_address    inet,
    user_agent    text,
    created_at    timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at    timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted    boolean                  default false not null,
    created_by    bigint
);

comment on table audit_logs is '审计日志表：存储系统操作的审计记录和安全日志';

comment on column audit_logs.id is '审计记录ID';

comment on column audit_logs.user_id is '操作用户ID';

comment on column audit_logs.action is '操作类型（应用层维护枚举映射）';

comment on column audit_logs.resource_type is '资源类型';

comment on column audit_logs.resource_id is '资源ID';

comment on column audit_logs.old_values is '操作前的数据';

comment on column audit_logs.new_values is '操作后的数据';

comment on column audit_logs.ip_address is '客户端IP地址';

comment on column audit_logs.user_agent is '客户端用户代理';

comment on column audit_logs.created_at is '操作时间';

comment on column audit_logs.updated_at is '更新时间';

comment on column audit_logs.is_deleted is '是否删除';

alter table audit_logs
    owner to be_eyes;

create index if not exists idx_audit_logs_user_created
    on audit_logs (user_id, created_at, action)
    where (is_deleted = false);

create index if not exists idx_audit_logs_resource_created
    on audit_logs (resource_type, resource_id, created_at)
    where (is_deleted = false);

create index if not exists idx_audit_logs_security
    on audit_logs (ip_address, action, created_at)
    where (is_deleted = false);

create index if not exists idx_audit_logs_retention
    on audit_logs (created_at)
    where (is_deleted = false);

alter table audit_logs
    add primary key (id);

create table if not exists scheduled_tasks
(
    id              bigint                                 not null,
    name            varchar(200)                           not null,
    description     text,
    task_type       smallint                               not null,
    cron_expression varchar(100)                           not null,
    task_config     jsonb,
    is_enabled      boolean                  default true,
    max_retry_count integer                  default 3,
    timeout_seconds integer                  default 300,
    last_run_at     timestamp with time zone,
    next_run_at     timestamp with time zone,
    last_run_status smallint,
    last_run_error  text,
    run_count       integer                  default 0,
    success_count   integer                  default 0,
    failure_count   integer                  default 0,
    created_at      timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at      timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted      boolean                  default false not null,
    created_by      bigint
);

comment on table scheduled_tasks is '定时任务表：存储系统定时任务的配置和执行状态';

comment on column scheduled_tasks.id is '定时任务ID';

comment on column scheduled_tasks.name is '任务名称';

comment on column scheduled_tasks.description is '任务描述';

comment on column scheduled_tasks.task_type is '任务类型（应用层维护枚举映射）';

comment on column scheduled_tasks.cron_expression is 'Cron表达式';

comment on column scheduled_tasks.task_config is '任务配置参数';

comment on column scheduled_tasks.is_enabled is '任务是否启用';

comment on column scheduled_tasks.max_retry_count is '最大重试次数';

comment on column scheduled_tasks.timeout_seconds is '超时时间（秒）';

comment on column scheduled_tasks.last_run_at is '最后执行时间';

comment on column scheduled_tasks.next_run_at is '下次执行时间';

comment on column scheduled_tasks.last_run_status is '最后执行状态（应用层维护枚举映射）';

comment on column scheduled_tasks.last_run_error is '最后执行错误信息';

comment on column scheduled_tasks.run_count is '执行次数';

comment on column scheduled_tasks.success_count is '成功次数';

comment on column scheduled_tasks.failure_count is '失败次数';

comment on column scheduled_tasks.created_at is '创建时间';

comment on column scheduled_tasks.updated_at is '更新时间';

comment on column scheduled_tasks.is_deleted is '是否删除';

comment on column scheduled_tasks.created_by is '创建者ID';

alter table scheduled_tasks
    owner to be_eyes;

create index if not exists idx_scheduled_tasks_enabled_status
    on scheduled_tasks (is_enabled, last_run_status, next_run_at)
    where (is_deleted = false);

create index if not exists idx_scheduled_tasks_next_run
    on scheduled_tasks (next_run_at)
    where ((is_enabled = true) AND (is_deleted = false));

alter table scheduled_tasks
    add primary key (id);

alter table scheduled_tasks
    add constraint unique_task_name
        unique (name);

create table if not exists servers
(
    id          bigint                                 not null,
    group_id    bigint,
    hostname    varchar(255),
    description text,
    status      smallint                 default 0,
    last_seen   timestamp with time zone,
    created_at  timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at  timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted  boolean                  default false not null,
    created_by  bigint,
    api_key     varchar(255),
    hardware_id bigint
);

comment on table servers is '服务器表：存储被监控服务器的基本信息';

comment on column servers.id is '服务器唯一标识';

comment on column servers.group_id is '所属服务器组ID';

comment on column servers.hostname is '主机名';

comment on column servers.description is '服务器描述';

comment on column servers.status is '服务器状态（应用层维护枚举映射）';

comment on column servers.last_seen is '最后在线时间';

comment on column servers.created_at is '创建时间';

comment on column servers.updated_at is '更新时间';

comment on column servers.is_deleted is '是否删除';

comment on column servers.created_by is '创建者ID';

comment on column servers.api_key is '客户端API密钥';

alter table servers
    owner to be_eyes;

create index if not exists idx_servers_api_key
    on servers (api_key);

create index if not exists idx_servers_status_active
    on servers (status, is_deleted, last_seen)
    where (is_deleted = false);

create index if not exists idx_servers_group_active
    on servers (group_id, is_deleted, status)
    where (is_deleted = false);

alter table servers
    add primary key (id);

alter table servers
    add constraint unique_server_hostname
        unique (hostname);

alter table servers
    add constraint unique_server_api_key
        unique (api_key);

create table if not exists server_hardware
(
    id             bigint                                 not null,
    os_name        varchar(100),
    os_version     varchar(100),
    kernel_version varchar(100),
    cpu_arch       varchar(50),
    cpu_name       varchar(200),
    cpu_cores      integer,
    total_memory   bigint,
    total_swap     bigint                   default 0,
    created_at     timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at     timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted     boolean                  default false not null,
    created_by     bigint
);

comment on table server_hardware is '服务器硬件信息表：存储服务器硬件配置信息';

comment on column server_hardware.id is '硬件信息ID';

comment on column server_hardware.os_name is '操作系统名称';

comment on column server_hardware.os_version is '操作系统版本';

comment on column server_hardware.kernel_version is '内核版本';

comment on column server_hardware.cpu_arch is 'CPU架构';

comment on column server_hardware.cpu_name is 'CPU型号';

comment on column server_hardware.cpu_cores is 'CPU核心数';

comment on column server_hardware.total_memory is '总内存(字节)';

comment on column server_hardware.total_swap is '总交换分区(字节)';

comment on column server_hardware.created_at is '创建时间';

comment on column server_hardware.updated_at is '更新时间';

comment on column server_hardware.is_deleted is '是否删除';

alter table server_hardware
    owner to be_eyes;

alter table server_hardware
    add constraint server_hardware_info_pkey
        primary key (id);

create table if not exists server_network_interfaces
(
    id             bigint                                 not null,
    server_id      bigint                                 not null,
    interface_name varchar(100)                           not null,
    ipv4_address   inet,
    ipv6_address   inet,
    is_active      boolean                  default true,
    created_at     timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at     timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted     boolean                  default false not null,
    created_by     bigint
);

comment on table server_network_interfaces is '服务器网络接口表：存储服务器网络接口信息';

comment on column server_network_interfaces.id is '网络接口ID';

comment on column server_network_interfaces.server_id is '关联服务器ID';

comment on column server_network_interfaces.interface_name is '接口名称';

comment on column server_network_interfaces.ipv4_address is 'IPv4地址';

comment on column server_network_interfaces.ipv6_address is 'IPv6地址（单个地址）';

comment on column server_network_interfaces.is_active is '接口是否活跃';

comment on column server_network_interfaces.created_at is '创建时间';

comment on column server_network_interfaces.updated_at is '更新时间';

comment on column server_network_interfaces.is_deleted is '是否删除';

alter table server_network_interfaces
    owner to be_eyes;

create unique index if not exists idx_server_network_interface_unique
    on server_network_interfaces (server_id, interface_name)
    where (is_deleted = false);

create index if not exists idx_server_network_interfaces_server
    on server_network_interfaces (server_id, is_deleted, is_active);

create index if not exists idx_server_network_interfaces_ipv4
    on server_network_interfaces (ipv4_address, is_deleted);

alter table server_network_interfaces
    add primary key (id);

create table if not exists server_disk
(
    id          bigint                                 not null,
    server_id   bigint                                 not null,
    disk_name   varchar(200)                           not null,
    file_system varchar(50),
    disk_kind   varchar(50),
    total_bytes bigint                                 not null,
    created_at  timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at  timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted  boolean                  default false not null,
    created_by  bigint
);

comment on table server_disk is '服务器磁盘信息表：存储服务器磁盘分区信息';

comment on column server_disk.id is '磁盘信息ID';

comment on column server_disk.server_id is '关联服务器ID';

comment on column server_disk.disk_name is '磁盘名称';

comment on column server_disk.file_system is '文件系统类型';

comment on column server_disk.disk_kind is '磁盘类型（SSD/HDD等）';

comment on column server_disk.total_bytes is '总容量(字节)';

comment on column server_disk.created_at is '创建时间';

comment on column server_disk.updated_at is '更新时间';

comment on column server_disk.is_deleted is '是否删除';

alter table server_disk
    owner to be_eyes;

create unique index if not exists idx_server_disk_unique
    on server_disk (server_id, disk_name)
    where (is_deleted = false);

create index if not exists idx_server_disk_info_server
    on server_disk (server_id, is_deleted);

alter table server_disk
    add constraint server_disk_info_pkey
        primary key (id);

