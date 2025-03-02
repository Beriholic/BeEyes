package xyz.beriholic.beeyes.controller;


import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import xyz.beriholic.beeyes.entity.RestBean;
import xyz.beriholic.beeyes.entity.vo.request.AccountPasswordChangeVO;
import xyz.beriholic.beeyes.entity.vo.request.ChangeAvatarVO;
import xyz.beriholic.beeyes.entity.vo.response.AccountVO;
import xyz.beriholic.beeyes.service.AccountService;


@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Resource
    AccountService accountService;

    @GetMapping("/me")
    public RestBean<AccountVO> me(){
        Long id= StpUtil.getLoginIdAsLong();
       AccountVO vo= accountService.getAccountInfoById(id);
       return RestBean.success(vo);
    }

    @PostMapping("/change/username")
    public RestBean<Void> changeUsername(
            @RequestParam("username") String username
    ){
        Long id=StpUtil.getLoginIdAsLong();
        accountService.changeUserNameById(id,username);
        return RestBean.success();
    }

    @PostMapping("/change/email")
    public RestBean<Void> changeEmail( @RequestParam("email") String email ) {
        Long id=StpUtil.getLoginIdAsLong();
        accountService.changeEmailById(id,email);
        return RestBean.success();
    }

    @PostMapping("/change/password")
    public RestBean<Void>changePassword(
            @RequestBody @Valid AccountPasswordChangeVO vo
    ){
        Long id=StpUtil.getLoginIdAsLong();
        accountService.changePasswordById(id,vo.getOld(),vo.getPassword());
        return RestBean.success();
    }
    @PostMapping("/change/avatar")
    public RestBean<Void> changeAvatar(
            @RequestBody ChangeAvatarVO vo
            ){
        Long id=StpUtil.getLoginIdAsLong();
        accountService.changeAvatar(id,vo.getAvatar());
        return  RestBean.success();
    }
}
