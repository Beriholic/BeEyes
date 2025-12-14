package cv.beriholic.beeyes.helper;

import cn.dev33.satoken.stp.StpUtil;
import cv.beriholic.beeyes.consts.PermissionCode;
import cv.beriholic.beeyes.consts.UserRoleCode;
import cv.beriholic.beeyes.exception.BizRuntimeException;
import cv.beriholic.beeyes.exception.ErrorCode;
import cv.beriholic.beeyes.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermissionValidateHelper {
    private final PermissionService permissionService;

    public void checkPermission(Long userId, PermissionCode permission) {
        boolean ok = permissionService.checkUserPermission(userId, permission);
        if (!ok) {
            throw new BizRuntimeException(ErrorCode.FORBIDDEN);
        }
    }

    public void checkPermission(Long userId, List<PermissionCode> permissions) {
        boolean ok = permissionService.checkUserPermission(userId, permissions);
        if (!ok) {
            throw new BizRuntimeException(ErrorCode.FORBIDDEN);
        }
    }

    public void checkUserRole(UserRoleCode userRoleCode) {
        Long userId = StpUtil.getLoginIdAsLong();
        boolean ok = permissionService.checkUserRole(userId, userRoleCode);
        if (!ok) {
            throw new BizRuntimeException(ErrorCode.FORBIDDEN);
        }
    }
}
