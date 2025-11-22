package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.models.entity.dto.AuthChangePasswordRequest;
import cv.beriholic.beeyes.models.entity.dto.AuthLoginRequest;

public interface AuthService {
    void login(AuthLoginRequest request);

    void changePassword(AuthChangePasswordRequest request);
}
