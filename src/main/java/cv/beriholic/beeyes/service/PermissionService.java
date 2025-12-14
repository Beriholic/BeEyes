package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.consts.PermissionCode;
import cv.beriholic.beeyes.consts.UserRoleCode;

import java.util.List;

public interface PermissionService {
    List<PermissionCode> getUserPermission(Long userId);

    boolean checkUserPermission(Long userId, List<PermissionCode> permissionCodes);

    boolean checkUserPermission(Long userId, PermissionCode permissionCodes);

    UserRoleCode getUserRole(Long userId);

    boolean checkUserRole(Long userId, UserRoleCode userRoleCode);
}
