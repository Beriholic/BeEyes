package cv.beriholic.beeyes.service.impl;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.IdUtil;
import cv.beriholic.beeyes.exception.BizRuntimeException;
import cv.beriholic.beeyes.exception.ErrorCode;
import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.entity.dto.*;
import cv.beriholic.beeyes.repository.UserRepository;
import cv.beriholic.beeyes.repository.UserRoleRepository;
import cv.beriholic.beeyes.service.ManageService;
import cv.beriholic.beeyes.utils.BcryptUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ManageServiceImpl implements ManageService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public PageDTO<List<ManageUserView>> getManageUserList(QueryManageUserListRequest request) {
        QueryManageUserSpec spec = new QueryManageUserSpec();
        spec.setEmail(request.getEmail());
        spec.setPhone(request.getPhone());
        spec.setFullName(request.getFullName());
        spec.setUsername(request.getUsername());
        spec.setRoleCode(request.getRuleCode());
        if (StringUtils.isNotEmpty(request.getParentId())) {
            spec.setParentId(Long.valueOf(request.getParentId()));
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateUserView createUser(CreateUserRequest request) {
        long id = IdUtil.getSnowflakeNextId();
        SaveUserInput saveUserInput = new SaveUserInput();
        saveUserInput.setId(id);
        saveUserInput.setEmail(request.getEmail());
        saveUserInput.setPhone(request.getPhone());
        saveUserInput.setFullName(request.getFullName());
        saveUserInput.setUsername(request.getUsername());
        saveUserInput.setParentId(Long.valueOf(request.getParentId()));
        Pair<String, String> randomHash = BcryptUtil.getRandomHash(8);
        saveUserInput.setPasswordHash(randomHash.getValue());

        SaveUserRoleInput saveUserRoleInput = new SaveUserRoleInput();
        saveUserRoleInput.setUserId(id);
        saveUserRoleInput.setRole(request.getRoleCode());

        userRepository.save(saveUserInput, SaveMode.INSERT_ONLY);
        userRoleRepository.save(saveUserRoleInput, SaveMode.INSERT_ONLY);

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
        long userId = Long.parseLong(request.getUserId());
        UpdateUserInput input = new UpdateUserInput();
        input.setId(userId);
        input.setEmail(request.getEmail());
        input.setPhone(request.getPhone());
        input.setFullName(request.getFullName());
        input.setUsername(request.getUsername());
        input.setParentId(Long.valueOf(request.getParentId()));


        UpdateUserRoleInput updateUserRoleInput = new UpdateUserRoleInput();
        updateUserRoleInput.setUserId(userId);
        updateUserRoleInput.setRole(request.getRoleCode());

        userRepository.save(input, SaveMode.UPDATE_ONLY);
        userRoleRepository.updateRole(updateUserRoleInput);
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
