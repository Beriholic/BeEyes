package xyz.beriholic.beeyes.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import xyz.beriholic.beeyes.entity.dto.Account;
import xyz.beriholic.beeyes.entity.dto.UserSession;
import xyz.beriholic.beeyes.exception.AuthorizationException;
import xyz.beriholic.beeyes.exception.ErrorCode;
import xyz.beriholic.beeyes.mapper.AccountMapper;
import xyz.beriholic.beeyes.model.response.AccountVO;
import xyz.beriholic.beeyes.model.response.AuthorizeVO;
import xyz.beriholic.beeyes.service.AccountService;
import xyz.beriholic.beeyes.utils.PasswordUtil;

import java.util.Objects;

import static xyz.beriholic.beeyes.utils.Const.USER_SESSION;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
    @Resource
    PasswordUtil passwordUtil;

    @Override
    public AuthorizeVO authenticate(String username, String password) {
        Account account = this.findAccountByNameOrEmail(username);

        if (Objects.isNull(account)) {
            throw new AuthorizationException(ErrorCode.USER_NAME_OR_EMAIL_NOT_FOUND);
        }

        if (!passwordUtil.check(password, account.getPassword())) {
            throw new AuthorizationException(ErrorCode.USER_NAME_OR_EMAIL_NOT_FOUND);
        }

        StpUtil.login(account.getId());
        StpUtil.getSession().set(
                USER_SESSION,
                new UserSession()
                        .setId(account.getId())
                        .setUsername(account.getUsername())
                        .setRole(account.getRole())
        );
        String token = StpUtil.getTokenValue();

        return new AuthorizeVO().setUsername(account.getUsername()).setRole(account.getRole()).setToken(token).setAvatar(account.getAvatar());
    }

    @Override
    public AccountVO getAccountInfoById(Long id) {
        Account account = this.getById(id);
        AccountVO vo = new AccountVO();
        BeanUtils.copyProperties(account, vo);
        return vo;
    }

    @Override
    public void changeUserNameById(Long id, String username) {
        this.update(Wrappers.<Account>update().eq("id", id).set("username", username));
    }

    @Override
    public void changeEmailById(Long id, String email) {
        this.update(Wrappers.<Account>update().eq("id", id).set("email", email));
    }

    @Override
    public void changePasswordById(Long id, String old, String password) {
        Account dbAccount = this.getById(id);

        if (Objects.isNull(dbAccount)) {
            throw new AuthorizationException(ErrorCode.USER_NAME_OR_EMAIL_NOT_FOUND);
        }

        if (!passwordUtil.check(old, dbAccount.getPassword())) {
            throw new AuthorizationException(ErrorCode.PASSWORD_ERROR);
        }

        String hash = passwordUtil.encrypt(password);

        this.update(Wrappers.<Account>update().eq("id", id).set("password", hash));
        StpUtil.logout();
    }

    @Override
    public void changeAvatar(Long id, String avatar) {
        this.update(Wrappers.<Account>update().eq("id", id).set("avatar", avatar));
    }

    public Account findAccountByNameOrEmail(String text) {
        return this.query()
                .eq("username", text).or()
                .eq("email", text)
                .one();
    }

    public String getUserMail() {
        return this.query().one().getEmail();
    }
}

