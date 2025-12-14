package cv.beriholic.beeyes.service.impl;

import cn.hutool.core.lang.Pair;
import cv.beriholic.beeyes.consts.PermissionCode;
import cv.beriholic.beeyes.consts.UserRoleCode;
import cv.beriholic.beeyes.exception.BizRuntimeException;
import cv.beriholic.beeyes.exception.ErrorCode;
import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.entity.dto.*;
import cv.beriholic.beeyes.repository.UserRepository;
import cv.beriholic.beeyes.service.ManageService;
import cv.beriholic.beeyes.utils.BcryptUtil;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ManageServiceImpl implements ManageService {

    private final UserRepository userRepository;

    @Override
    public PageDTO<List<ManageUserView>> getManageUserList(QueryManageUserListRequest request) {
        QueryManageUserSpec spec = new QueryManageUserSpec();
        spec.setEmail(request.getEmail());
        spec.setPhone(request.getPhone());
        spec.setFullName(request.getFullName());
        spec.setUsername(request.getUsername());
        spec.setRoleCode(request.getRuleCode());

        Page<ManageUserView> page = userRepository.findBySpecFetchPage(spec, request.getPageIndex(), request.getPageSize(), ManageUserView.class);

        return PageDTO.of(
                page.getRows(),
                request.getPageIndex(),
                request.getPageSize(),
                page.getTotalRowCount(),
                page.getTotalPageCount()
        );
    }

    @Override
    public CreateUserView createUser(CreateUserRequest request) {
        SaveUserInput input = new SaveUserInput();
        input.setEmail(request.getEmail());
        input.setPhone(request.getPhone());
        input.setFullName(request.getFullName());
        input.setRoleCode(request.getRoleCode());
        input.setUsername(request.getUsername());

        Pair<String, String> randomHash = BcryptUtil.getRandomHash(8);
        input.setPasswordHash(randomHash.getValue());


        if (UserRoleCode.SUPER_ADMIN.getCode() == request.getRoleCode() || UserRoleCode.ADMIN.getCode() == request.getRoleCode()) {
            List<SaveUserInput.TargetOf_permissions> permissions = Arrays.stream(PermissionCode.values()).map(it -> {
                SaveUserInput.TargetOf_permissions targetOfPermissions = new SaveUserInput.TargetOf_permissions();
                targetOfPermissions.setPermissionCode(it.getCode());
                return targetOfPermissions;
            }).toList();
            input.setPermissions(permissions);
        }

        userRepository.save(input, SaveMode.INSERT_ONLY);

        CreateUserView createUserView = new CreateUserView();
        createUserView.setEmail(request.getEmail());
        createUserView.setPhone(request.getPhone());
        createUserView.setFullName(request.getFullName());
        createUserView.setUsername(request.getUsername());
        createUserView.setRoleCode(request.getRoleCode());
        createUserView.setPassword(randomHash.getKey());

        return createUserView;
    }

    @Override
    public void updateUser(UpdateUserRequest request) {
        UpdateUserInput input = new UpdateUserInput();
        input.setId(Long.valueOf(request.getUserId()));
        input.setEmail(request.getEmail());
        input.setPhone(request.getPhone());
        input.setFullName(request.getFullName());
        input.setRoleCode(request.getRoleCode());
        input.setUsername(request.getUsername());

        userRepository.save(input, SaveMode.UPDATE_ONLY);

    }

    @Override
    public void deleteUser(DeleteUserRequest request) {
        userRepository.deleteById(Long.valueOf(request.getUserId()));
    }

    @Override
    public ResetUserView resetUserPassword(ResetUserPasswordRequest request) {
        Long userId = Long.valueOf(request.getUserId());
        ResetUserView userView = userRepository.findById(userId, ResetUserView.class);
        if (Objects.isNull(userView)) {
            throw new BizRuntimeException(ErrorCode.RECORD_NOT_FOUND);
        }

        Pair<String, String> randomHash = BcryptUtil.getRandomHash(8);
        userView.setPassword(randomHash.getKey());

        userRepository.updatePasswordHash(userId, randomHash.getValue());
        return userView;
    }
}
