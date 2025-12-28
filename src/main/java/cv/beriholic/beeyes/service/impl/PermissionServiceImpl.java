package cv.beriholic.beeyes.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import cv.beriholic.beeyes.consts.PermissionCode;
import cv.beriholic.beeyes.consts.UserRoleCode;
import cv.beriholic.beeyes.exception.BizRuntimeException;
import cv.beriholic.beeyes.exception.ErrorCode;
import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.entity.Immutables;
import cv.beriholic.beeyes.models.entity.PermissionsDO;
import cv.beriholic.beeyes.models.entity.UserDO;
import cv.beriholic.beeyes.models.entity.UserRoleDO;
import cv.beriholic.beeyes.models.entity.dto.ManageUserView;
import cv.beriholic.beeyes.models.entity.dto.QueryManageUserSpec;
import cv.beriholic.beeyes.models.entity.dto.QueryPermissionUserListRequest;
import cv.beriholic.beeyes.models.entity.dto.SetPermissionRequest;
import cv.beriholic.beeyes.repository.PermissionsRepository;
import cv.beriholic.beeyes.repository.UserRepository;
import cv.beriholic.beeyes.repository.UserRoleRepository;
import cv.beriholic.beeyes.service.PermissionService;
import cv.beriholic.beeyes.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionsRepository permissionsRepository;
    private final UserRoleRepository userRoleRepository;
    private final ProfileService profileService;
    private final UserRepository userRepository;

    @Override
    public List<PermissionCode> getUserPermission(Long userId) {
        List<PermissionsDO> permissionsDOS = permissionsRepository.findByUserId(userId);
        if (CollectionUtil.isEmpty(permissionsDOS)) {
            return Collections.emptyList();
        }
        return permissionsDOS.stream()
                .map(it ->
                        PermissionCode.getByCode(it.permission())
                ).toList();
    }

    @Override
    public boolean checkUserPermission(Long userId, List<PermissionCode> permissionCodeList) {
        List<Short> permissionCodes = permissionCodeList.stream().map(PermissionCode::getCode).toList();
        return permissionsRepository.existUserPermissions(userId, permissionCodes);
    }

    @Override
    public boolean checkUserPermission(Long userId, PermissionCode permissionCodes) {
        UserRoleCode userRole = getUserRole(userId);
        if (userRole.equals(UserRoleCode.SUPER_ADMIN)) {
            return true;
        }
        return checkUserPermission(userId, Collections.singletonList(permissionCodes));
    }

    @Override
    public UserRoleCode getUserRole(Long userId) {
        UserRoleDO userRole = userRoleRepository.findById(userId);
        if (Objects.isNull(userRole)) {
            throw new BizRuntimeException(ErrorCode.RECORD_NOT_FOUND);
        }
        return UserRoleCode.getByCode(userRole.role());
    }

    public boolean checkUserRole(Long userId, UserRoleCode userRoleCode) {
        return userRoleRepository.existUserRole(userId, userRoleCode.getCode());
    }

    @Override
    @Transactional
    public void setPermission(SetPermissionRequest request) {
        Long parentId = StpUtil.getLoginIdAsLong();
        long userId = Long.parseLong(request.getUserId());
        UserRoleCode userRole = getUserRole(parentId);
        if (Objects.isNull(userRole)) {
            throw new BizRuntimeException(ErrorCode.RECORD_NOT_FOUND);
        }

        switch (userRole) {
            case ADMIN -> {
                profileService.checkParentUser(parentId, userId);
            }
            case USER -> {
                throw new BizRuntimeException(ErrorCode.UNAUTHORIZED);
            }
        }

        List<PermissionsDO> permissions = Lists.newArrayList();
        for (short code : request.getPermissionCode()) {
            PermissionsDO permissionsDO = Immutables.createPermissionsDO(draft -> {
                draft.setPermission(code);
                draft.setGrantedBy(parentId);
                draft.setCreatedAt(LocalDateTime.now());
            });
            permissions.add(permissionsDO);
        }

        UserDO user = Immutables.createUserDO(draft -> {
            draft.setId(userId);
            draft.setPermissions(permissions);
        });
        userRepository.save(user, SaveMode.UPSERT);
    }


    @Override
    public PageDTO<List<ManageUserView>> getManageUserList(QueryPermissionUserListRequest request) {
        QueryManageUserSpec spec = new QueryManageUserSpec();
        spec.setEmail(request.getEmail());
        spec.setPhone(request.getPhone());
        spec.setFullName(request.getFullName());
        spec.setUsername(request.getUsername());
        spec.setRoleCode(request.getRuleCode());

        Long currentId = StpUtil.getLoginIdAsLong();
        UserRoleCode userRole = getUserRole(currentId);
        if (Objects.requireNonNull(userRole) == UserRoleCode.ADMIN) {
            spec.setParentId(currentId);
        }

        Page<ManageUserView> page = userRepository.findBySpecFetchPage(spec, request.getPageIndex(), request.getPageSize(), ManageUserView.class);

        return PageDTO.of(
                page.getRows(),
                request.getPageIndex(),
                request.getPageSize(),
                page.getTotalRowCount(),
                page.getTotalPageCount()
        );
    }
}
