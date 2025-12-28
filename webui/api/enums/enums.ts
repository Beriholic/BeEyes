export const UserRole = {
  SUPER_ADMIN: { key: "SUPER_ADMIN", code: 1 },
  ADMIN: { key: "ADMIN", code: 2 },
  USER: { key: "USER", code: 3 },
} as const;

export type UserRoleKey = keyof typeof UserRole;
export type UserRoleType = (typeof UserRole)[UserRoleKey];

export const Permission = {
  CREATE_SERVER: { key: "创建机器", code: 0 },
  UPDATE_SERVER: { key: "更新机器", code: 1 },
  DELETE_SERVER: { key: "删除机器", code: 2 },
  SSH_CONNECT: { key: "SSH连接", code: 3 },
  ALERT_MANAGE: { key: "告警管理", code: 4 },
} as const;

export type PermissionKey = keyof typeof Permission;
export type PermissionType = (typeof Permission)[PermissionKey];
