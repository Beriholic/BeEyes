package cv.beriholic.beeyes.controller;

import cn.dev33.satoken.stp.StpUtil;
import cv.beriholic.beeyes.consts.PermissionCode;
import cv.beriholic.beeyes.consts.UserRoleCode;
import cv.beriholic.beeyes.helper.PermissionValidateHelper;
import cv.beriholic.beeyes.helper.ValidateHelper;
import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.models.entity.dto.ManageUserView;
import cv.beriholic.beeyes.models.entity.dto.QueryPermissionUserListRequest;
import cv.beriholic.beeyes.models.entity.dto.SetPermissionRequest;
import cv.beriholic.beeyes.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;
    private final PermissionValidateHelper permissionValidateHelper;

    @GetMapping("/enum")
    public RestBean<List<PermissionCode>> getPermissionEnum() {
        permissionValidateHelper.checkUserRole(UserRoleCode.SUPER_ADMIN, UserRoleCode.ADMIN);
        return RestBean.success(Arrays.stream(PermissionCode.values()).toList());
    }

    @PostMapping("/user-list")
    public RestBean<PageDTO<List<ManageUserView>>> getManageUserList(
            @RequestBody QueryPermissionUserListRequest request
    ) {
        permissionValidateHelper.checkUserRole(UserRoleCode.SUPER_ADMIN, UserRoleCode.ADMIN);

        ValidateHelper.validateQueryPageParam(request.getPageIndex(), request.getPageSize());

        PageDTO<List<ManageUserView>> manageUserViewList = permissionService.getManageUserList(request);
        return RestBean.success(manageUserViewList);

    }

    @GetMapping("/user")
    public RestBean<List<PermissionCode>> getUserPermissions(String userId) {
        permissionValidateHelper.checkUserRole(UserRoleCode.SUPER_ADMIN, UserRoleCode.ADMIN);
        List<PermissionCode> userPermission = permissionService.getUserPermission(Long.valueOf(userId));
        return RestBean.success(userPermission);
    }

    @GetMapping("/current-permission")
    public RestBean<List<Short>> getCurrentUserPermissions() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<PermissionCode> userPermission = permissionService.getUserPermission(userId);
        return RestBean.success(
                userPermission.stream().map(PermissionCode::getCode).toList()
        );
    }

    @GetMapping("/current-role")
    public RestBean<UserRoleCode> getCurrentUserRole() {
        Long userId = StpUtil.getLoginIdAsLong();
        UserRoleCode userRole = permissionService.getUserRole(userId);
        return RestBean.success(userRole);
    }

    @PostMapping("/set")
    public RestBean<Void> setPermission(SetPermissionRequest request) {
        permissionValidateHelper.checkUserRole(UserRoleCode.SUPER_ADMIN, UserRoleCode.ADMIN);
        permissionService.setPermission(request);
        return RestBean.success();
    }

}
