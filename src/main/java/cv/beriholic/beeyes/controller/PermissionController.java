package cv.beriholic.beeyes.controller;

import cn.dev33.satoken.stp.StpUtil;
import cv.beriholic.beeyes.consts.PermissionCode;
import cv.beriholic.beeyes.consts.UserRoleCode;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping("/current-permission")
    public RestBean<List<PermissionCode>> getCurrentUserPermissions() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<PermissionCode> userPermission = permissionService.getUserPermission(userId);
        return RestBean.success(userPermission);
    }

    @GetMapping("/current-role")
    public RestBean<UserRoleCode> getCurrentUserRole() {
        Long userId = StpUtil.getLoginIdAsLong();
        UserRoleCode userRole = permissionService.getUserRole(userId);
        return RestBean.success(userRole);
    }
}
