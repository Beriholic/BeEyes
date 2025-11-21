package cv.beriholic.beeyes.controller;

import cn.dev33.satoken.stp.StpUtil;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.models.entity.dto.UserBaseView;
import cv.beriholic.beeyes.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/info")
    public RestBean<UserBaseView> getSelfInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        UserBaseView userBaseView = profileService.getProfileById(userId);
        return RestBean.success(userBaseView);
    }
}
