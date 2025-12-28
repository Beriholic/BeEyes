package cv.beriholic.beeyes.service.impl;

import cv.beriholic.beeyes.exception.BizRuntimeException;
import cv.beriholic.beeyes.exception.ErrorCode;
import cv.beriholic.beeyes.models.entity.dto.UserBaseView;
import cv.beriholic.beeyes.repository.UserRepository;
import cv.beriholic.beeyes.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;

    @Override
    public UserBaseView getProfileById(Long userId) {
        return userRepository.findById(userId, UserBaseView.class);
    }

    @Override
    public void checkParentUser(Long parentId, long userId) {
        boolean ok = userRepository.checkParent(parentId, userId);
        if (!ok) {
            throw new BizRuntimeException(ErrorCode.UNAUTHORIZED);
        }
    }
}
