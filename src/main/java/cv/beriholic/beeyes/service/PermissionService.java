package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.consts.PermissionCode;
import cv.beriholic.beeyes.consts.UserRoleCode;
import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.entity.dto.ManageUserView;
import cv.beriholic.beeyes.models.entity.dto.QueryPermissionUserListRequest;
import cv.beriholic.beeyes.models.entity.dto.SetPermissionRequest;

import java.util.List;

public interface PermissionService {
    List<PermissionCode> getUserPermission(Long userId);

    boolean checkUserPermission(Long userId, List<PermissionCode> permissionCodes);

    boolean checkUserPermission(Long userId, PermissionCode permissionCodes);

    UserRoleCode getUserRole(Long userId);

    boolean checkUserRole(Long userId, UserRoleCode userRoleCode);

    void setPermission(SetPermissionRequest request);

    PageDTO<List<ManageUserView>> getManageUserList(QueryPermissionUserListRequest request);
}
