package cv.beriholic.beeyes.helper;

import cn.dev33.satoken.stp.StpUtil;
import cv.beriholic.beeyes.consts.PermissionCode;
import cv.beriholic.beeyes.consts.UserRoleCode;
import cv.beriholic.beeyes.exception.BizRuntimeException;
import cv.beriholic.beeyes.exception.ErrorCode;
import cv.beriholic.beeyes.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionValidateHelper {
    private final PermissionService permissionService;

    public void checkPermission(PermissionCode permission) {
        long userId = StpUtil.getLoginIdAsLong();
        boolean ok = permissionService.checkUserPermission(userId, permission);
        if (!ok) {
            throw new BizRuntimeException(ErrorCode.FORBIDDEN);
        }
    }

    public void checkPermission(List<PermissionCode> permissions) {
        long userId = StpUtil.getLoginIdAsLong();
        boolean ok = permissionService.checkUserPermission(userId, permissions);
        if (!ok) {
            throw new BizRuntimeException(ErrorCode.FORBIDDEN);
        }
    }

    public void checkPermission(Long userId, PermissionCode permission) {
        boolean ok = permissionService.checkUserPermission(userId, permission);
        log.info("Permission check: userId={}, permission={}, result={}", userId, permission, ok);
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

    public void checkUserRole(Long userId, UserRoleCode... userRoleCode) {
        for (UserRoleCode code : userRoleCode) {
            boolean ok = permissionService.checkUserRole(userId, code);
            if (!ok) {
                throw new BizRuntimeException(ErrorCode.FORBIDDEN);
            }
        }
    }

    public void checkUserRole(UserRoleCode... userRoleCode) {
        Long userId = StpUtil.getLoginIdAsLong();

        for (UserRoleCode code : userRoleCode) {
            boolean ok = permissionService.checkUserRole(userId, code);
            if (ok) {
                return;
            }
        }
        throw new BizRuntimeException(ErrorCode.FORBIDDEN);
    }
}
