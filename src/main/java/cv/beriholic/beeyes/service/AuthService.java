package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.models.entity.dto.AuthLoginRequest;
import cv.beriholic.beeyes.models.request.AuthChangePasswordRequest;

public interface AuthService {
    void login(AuthLoginRequest request);

    void changePassword(AuthChangePasswordRequest request);
}
