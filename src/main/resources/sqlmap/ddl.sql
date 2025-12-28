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

create index if not exists idx_permissions_user_active
    on permissions (user_id, permission, is_deleted)
    where (is_deleted = false);

alter table permissions
    add primary key (id);

alter table permissions
    add constraint unique_user_permission
        unique (user_id, permission);

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

create index if not exists idx_server_disk_info_server
    on server_disk (server_id, is_deleted);

create unique index if not exists idx_server_disk_unique
    on server_disk (server_id, disk_name)
    where (is_deleted = false);

alter table server_disk
    add constraint server_disk_info_pkey
        primary key (id);

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
    add constraint server_hardware_info_pkey
        primary key (id);

create table if not exists server_network_interfaces
(
    id             bigint                                 not null,
    server_id      bigint                                 not null,
    interface_name varchar(100)                           not null,
    ipv4_address   varchar(100)[],
    ipv6_address   varchar(100)[],
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

comment on column server_network_interfaces.ipv6_address is 'IPv6地址';

comment on column server_network_interfaces.created_at is '创建时间';

comment on column server_network_interfaces.updated_at is '更新时间';

comment on column server_network_interfaces.is_deleted is '是否删除';

create unique index if not exists idx_server_network_interface_unique
    on server_network_interfaces (server_id, interface_name)
    where (is_deleted = false);

alter table server_network_interfaces
    add primary key (id);

create table if not exists servers
(
    id             bigint                                 not null,
    group_id       bigint,
    hostname       varchar(255),
    description    text,
    status         smallint                 default 0,
    last_seen      timestamp with time zone,
    created_at     timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at     timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted     boolean                  default false not null,
    created_by     bigint,
    api_key        varchar(255),
    hardware_id    bigint,
    region         varchar(20),
    os_name        varchar(100),
    os_version     varchar(100),
    kernel_version varchar(100)
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

create index if not exists idx_servers_api_key
    on servers (api_key);

create index if not exists idx_servers_group_active
    on servers (group_id, is_deleted, status)
    where (is_deleted = false);

create index if not exists idx_servers_status_active
    on servers (status, is_deleted, last_seen)
    where (is_deleted = false);

create index if not exists servers_region_index
    on servers (region);

alter table servers
    add primary key (id);

alter table servers
    add constraint unique_server_api_key
        unique (api_key);

alter table servers
    add constraint unique_server_hostname
        unique (hostname);

create table if not exists ssh_connections
(
    server_id          bigint                                 not null,
    name               varchar(100)                           not null,
    port               integer                  default 22    not null,
    created_at         timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at         timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted         boolean                  default false not null,
    created_by         bigint,
    password           varchar(100)                           not null,
    user_id            bigint                                 not null,
    last_connection_at timestamp
);

comment on table ssh_connections is 'SSH连接表：存储服务器SSH连接配置信息和认证方式';

comment on column ssh_connections.server_id is '关联服务器ID';

comment on column ssh_connections.name is 'SSH用户名';

comment on column ssh_connections.port is 'SSH端口号';

comment on column ssh_connections.created_at is '创建时间';

comment on column ssh_connections.updated_at is '更新时间';

comment on column ssh_connections.is_deleted is '是否删除';

comment on column ssh_connections.created_by is '创建者ID';

alter table ssh_connections
    add constraint ssh_connections_pk
        primary key (server_id, user_id);

create table if not exists user_role
(
    id         bigint                                 not null,
    role       smallint                               not null,
    granted_by bigint,
    granted_at timestamp with time zone default CURRENT_TIMESTAMP,
    created_at timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted boolean                  default false not null,
    created_by bigint,
    user_id    bigint                                 not null
);

create index if not exists user_role_user_index
    on user_role (user_id);

alter table user_role
    add constraint user_roles_pk
        primary key (id);

create table if not exists users
(
    id            bigint                                 not null,
    parent_id     bigint,
    username      varchar(50)                            not null,
    email         varchar(255)                           not null,
    password_hash varchar(255)                           not null,
    full_name     varchar(100),
    created_at    timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at    timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted    boolean                  default false not null,
    created_by    bigint,
    phone         varchar(20)
);

comment on table users is '用户表：存储系统用户基本信息和账户状态';

comment on column users.id is '用户唯一标识';

comment on column users.parent_id is '父账户ID（用于账户关联）';

comment on column users.username is '用户名（唯一）';

comment on column users.email is '邮箱地址（唯一）';

comment on column users.password_hash is '密码哈希值';

comment on column users.full_name is '用户全名';

comment on column users.created_at is '创建时间';

comment on column users.updated_at is '更新时间';

comment on column users.is_deleted is '是否删除';

comment on column users.created_by is '创建者ID';

comment on column users.phone is '电话号码';

create index if not exists idx_users_parent_id
    on users (parent_id);

alter table users
    add primary key (id);

alter table users
    add unique (email);

alter table users
    add unique (username);

create table if not exists alert_rules
(
    id               bigint                                 not null,
    name             varchar(100)                           not null,
    server_id        bigint,
    metric_type      smallint                               not null,
    condition        smallint                               not null,
    threshold        double precision                       not null,
    duration_seconds integer,
    enabled          boolean                  default true  not null,
    silence_seconds  integer                  default 0,
    created_at       timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at       timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted       boolean                  default false not null,
    created_by       bigint
);

comment on table alert_rules is '告警规则表：存储用户自定义的告警规则';
comment on column alert_rules.id is '规则ID';
comment on column alert_rules.name is '规则名称';
comment on column alert_rules.server_id is '关联服务器ID（为空则对所有服务器生效）';
comment on column alert_rules.metric_type is '指标类型（CPU/内存/磁盘/状态）';
comment on column alert_rules.condition is '判断条件（大于/小于/等于）';
comment on column alert_rules.threshold is '阈值';
comment on column alert_rules.duration_seconds is '持续时间（秒）';
comment on column alert_rules.enabled is '是否启用';
comment on column alert_rules.silence_seconds is '沉默时间（秒）';
comment on column alert_rules.created_at is '创建时间';
comment on column alert_rules.updated_at is '更新时间';
comment on column alert_rules.is_deleted is '是否删除';
comment on column alert_rules.created_by is '创建者ID';

create index if not exists idx_alert_rules_server
    on alert_rules (server_id, is_deleted, enabled);

alter table alert_rules
    add primary key (id);

create table if not exists alert_logs
(
    id           bigint                                 not null,
    rule_id      bigint                                 not null,
    server_id    bigint                                 not null,
    metric_value double precision,
    message      text,
    status       smallint                               not null,
    started_at   timestamp with time zone default CURRENT_TIMESTAMP,
    resolved_at  timestamp with time zone,
    created_at   timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at   timestamp with time zone default CURRENT_TIMESTAMP,
    is_deleted   boolean                  default false not null,
    created_by   bigint
);


comment on table alert_logs is '告警日志表：存储告警触发和恢复的历史记录';
comment on column alert_logs.id is '日志ID';
comment on column alert_logs.rule_id is '关联规则ID';
comment on column alert_logs.server_id is '关联服务器ID';
comment on column alert_logs.metric_value is '触发时的指标值';
comment on column alert_logs.message is '告警内容';
comment on column alert_logs.status is '告警状态（触发中/已恢复）';
comment on column alert_logs.started_at is '开始时间';
comment on column alert_logs.resolved_at is '恢复时间';
comment on column alert_logs.created_at is '创建时间';
comment on column alert_logs.updated_at is '更新时间';
comment on column alert_logs.is_deleted is '是否删除';

create index if not exists idx_alert_logs_server_status
    on alert_logs (server_id, status, is_deleted);

alter table alert_logs
    add primary key (id);
