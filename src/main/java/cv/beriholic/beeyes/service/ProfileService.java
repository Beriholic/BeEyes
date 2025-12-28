package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.models.entity.dto.UserBaseView;

public interface ProfileService {
    UserBaseView getProfileById(Long userId);

    void checkParentUser(Long parentId, long userId);
}
