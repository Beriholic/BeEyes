package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.models.request.auth.AuthChangePasswordRequest;
import cv.beriholic.beeyes.models.request.auth.AuthLoginRequest;

public interface AuthService {
    void login(AuthLoginRequest request);

    void changePassword(AuthChangePasswordRequest request);
}
