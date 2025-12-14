package cv.beriholic.beeyes.controller;

import cn.dev33.satoken.stp.StpUtil;
import cv.beriholic.beeyes.consts.UserRoleCode;
import cv.beriholic.beeyes.helper.PermissionValidateHelper;
import cv.beriholic.beeyes.helper.ValidateHelper;
import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.models.entity.dto.*;
import cv.beriholic.beeyes.service.ManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/manage")
@RequiredArgsConstructor
public class ManageController {
    private final PermissionValidateHelper permissionValidateHelper;
    private final ManageService manageService;

    @PostMapping("/list")
    public RestBean<PageDTO<List<ManageUserView>>> list(
            @RequestBody QueryManageUserListRequest request
    ) {
        permissionValidateHelper.checkUserRole(UserRoleCode.SUPER_ADMIN);

        ValidateHelper.validateQueryPageParam(request.getPageIndex(), request.getPageSize());

        PageDTO<List<ManageUserView>> manageUserViewList = manageService.getManageUserList(request);
        return RestBean.success(manageUserViewList);
    }

    @PostMapping("/user-create")
    public RestBean<CreateUserView> createUser(
            @RequestBody CreateUserRequest request
    ) {
        permissionValidateHelper.checkUserRole(UserRoleCode.SUPER_ADMIN);
        ValidateHelper.validateCreateUserRequest(request);
        CreateUserView createUserView = manageService.createUser(request);
        return RestBean.success(createUserView);
    }

    @PutMapping("/user-update")
    public RestBean<Void> updateUser(
            @RequestBody UpdateUserRequest request
    ) {
        permissionValidateHelper.checkUserRole(UserRoleCode.SUPER_ADMIN);
        ValidateHelper.validateUpdateUserRequest(request);
        manageService.updateUser(request);
        return RestBean.success();
    }

    @DeleteMapping("/user-delete")
    public RestBean<Void> deleteUser(
            @RequestBody DeleteUserRequest request
    ) {
        permissionValidateHelper.checkUserRole(UserRoleCode.SUPER_ADMIN);
        ValidateHelper.validateDeleteUserRequest(request);
        manageService.deleteUser(request);
        return RestBean.success();
    }

    @PutMapping("/user-reset-password")
    public RestBean<ResetUserView> resetUserPassword(
            @RequestBody ResetUserPasswordRequest request
    ) {
        permissionValidateHelper.checkUserRole(UserRoleCode.SUPER_ADMIN);
        ValidateHelper.validateResetUserPasswordRequest(request);
        ResetUserView resetUserView = manageService.resetUserPassword(request);
        StpUtil.logout(request.getUserId());
        return RestBean.success(resetUserView);
    }
}
