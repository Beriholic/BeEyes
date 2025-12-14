export const UserRole = {
  SUPER_ADMIN: { key: "SUPER_ADMIN", code: 1 },
  ADMIN: { key: "ADMIN", code: 2 },
  USER: { key: "USER", code: 3 },
} as const;

export type UserRoleKey = keyof typeof UserRole;
export type UserRoleType = (typeof UserRole)[UserRoleKey];

export const Permission = {
  CREATE_SERVER: { key: "CREATE_SERVER", code: 0 },
  UPDATE_SERVER: { key: "UPDATE_SERVER", code: 1 },
  DELETE_SERVER: { key: "DELETE_SERVER", code: 2 },
} as const;

export type PermissionKey = keyof typeof Permission;
export type PermissionType = (typeof Permission)[PermissionKey];
