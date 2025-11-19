package cv.beriholic.beeyes.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cv.beriholic.beeyes.ContextHolder;
import cv.beriholic.beeyes.exception.AuthorizationException;
import cv.beriholic.beeyes.exception.ErrorCode;
import cv.beriholic.beeyes.models.entity.Immutables;
import cv.beriholic.beeyes.models.entity.UserDO;
import cv.beriholic.beeyes.models.entity.dto.AuthUserSpec;
import cv.beriholic.beeyes.models.entity.dto.AuthUserView;
import cv.beriholic.beeyes.models.request.auth.AuthChangePasswordRequest;
import cv.beriholic.beeyes.models.request.auth.AuthLoginRequest;
import cv.beriholic.beeyes.repository.UserRepository;
import cv.beriholic.beeyes.service.AuthService;
import cv.beriholic.beeyes.utils.BcryptUtil;
import cv.beriholic.beeyes.utils.JsonUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.exception.EmptyResultException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Resource
    private UserRepository userRepository;

    @Override
    public void login(AuthLoginRequest request) {
        log.info("[AuthService] login biz start, input:{}", JsonUtil.toJSONString(request));

        AuthUserSpec spec = new AuthUserSpec();
        spec.setUsername(request.getUsername());
        spec.setEmail(request.getEmail());
        spec.setPhone(request.getPhone());
        try {
            AuthUserView authUserView = userRepository.findByAuthSpec(spec);


            log.info("debug password: {}", BcryptUtil.encrypt(request.getPassword()));

            if (!BcryptUtil.check(request.getPassword(), authUserView.getPasswordHash())) {
                throw new AuthorizationException(ErrorCode.UNAUTHORIZED.getCode(), "密码错误");
            }

            StpUtil.login(authUserView.getId());
        } catch (EmptyResultException e) {
            throw new AuthorizationException(ErrorCode.UNAUTHORIZED.getCode(), "用户不存在或用户名错误");
        }
    }

    @Override
    public void changePassword(AuthChangePasswordRequest request) {
        AuthUserView authUSerView = userRepository.findById(ContextHolder.getCurrent().getUserId(), AuthUserView.class);
        if (Objects.isNull(authUSerView)) {
            throw new AuthorizationException(ErrorCode.UNAUTHORIZED.getCode(), "用户不存在");
        }

        if (!BcryptUtil.check(request.getOldPassword(), authUSerView.getPasswordHash())) {
            throw new AuthorizationException(ErrorCode.UNAUTHORIZED.getCode(), "旧密码错误");
        }

        UserDO userDO = Immutables.createUserDO(draft -> {
            draft.setId(authUSerView.getId());
            draft.setPasswordHash(BcryptUtil.encrypt(request.getNewPassword()));
        });
        userRepository.save(userDO);
        StpUtil.logout();
    }
}
