package cv.beriholic.beeyes.service.impl;

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
}
