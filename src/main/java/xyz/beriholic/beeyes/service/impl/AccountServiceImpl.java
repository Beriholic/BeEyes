package xyz.beriholic.beeyes.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import xyz.beriholic.beeyes.entity.dto.Account;
import xyz.beriholic.beeyes.entity.dto.UserSession;
import xyz.beriholic.beeyes.entity.vo.request.ConfirmResetVO;
import xyz.beriholic.beeyes.entity.vo.request.EmailResetVO;
import xyz.beriholic.beeyes.entity.vo.response.AuthorizeVO;
import xyz.beriholic.beeyes.exception.UserNameOrEmailNotFound;
import xyz.beriholic.beeyes.exception.UserNameOrPasswordError;
import xyz.beriholic.beeyes.mapper.AccountMapper;
import xyz.beriholic.beeyes.service.AccountService;
import xyz.beriholic.beeyes.utils.Const;
import xyz.beriholic.beeyes.utils.FlowUtils;
import xyz.beriholic.beeyes.utils.PasswordUtil;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static xyz.beriholic.beeyes.utils.Const.USER_SESSION;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Value("${spring.web.verify.mail-limit}")
    int verifyLimit;

    @Resource
    AmqpTemplate rabbitTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    PasswordUtil passwordUtil;

    @Resource
    FlowUtils flow;

    @Value("${spring.security.jwt.limit.base}")
    private int limit_base;
    @Value("${spring.security.jwt.limit.upgrade}")
    private int limit_upgrade;
    @Value("${spring.security.jwt.limit.frequency}")
    private int limit_frequency;

    public String registerEmailVerifyCode(String type, String email, String address) {
        synchronized (address.intern()) {
            if (!this.verifyLimit(address))
                return "请求频繁，请稍后再试";
            Random random = new Random();
            int code = random.nextInt(899999) + 100000;
            Map<String, Object> data = Map.of("type", type, "email", email, "code", code);
            rabbitTemplate.convertAndSend(Const.MQ_MAIL, data);
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }
    }

    @Override
    public String resetEmailAccountPassword(EmailResetVO info) {
        String verify = resetConfirm(new ConfirmResetVO(info.getEmail(), info.getCode()));
        if (verify != null) return verify;
        String email = info.getEmail();
        String password = passwordUtil.encrypt(info.getPassword());
        boolean update = this.update().eq("email", email).set("password", password).update();
        if (update) {
            this.deleteEmailVerifyCode(email);
        }
        return update ? null : "更新失败，请联系管理员";
    }

    @Override
    public String resetConfirm(ConfirmResetVO info) {
        String email = info.getEmail();
        String code = this.getEmailVerifyCode(email);
        if (code == null) return "请先获取验证码";
        if (!code.equals(info.getCode())) return "验证码错误，请重新输入";
        return null;
    }

    @Override
    public AuthorizeVO authenticate(String username, String password) {
        Account account = this.findAccountByNameOrEmail(username);

        if (Objects.isNull(account)) {
            throw new UserNameOrEmailNotFound();
        }

        if (!passwordUtil.check(password, account.getPassword())) {
            throw new UserNameOrPasswordError();
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

        return new AuthorizeVO().setUsername(account.getUsername()).setRole(account.getRole()).setToken(token);
    }

    private void deleteEmailVerifyCode(String email) {
        String key = Const.VERIFY_EMAIL_DATA + email;
        stringRedisTemplate.delete(key);
    }

    private String getEmailVerifyCode(String email) {
        String key = Const.VERIFY_EMAIL_DATA + email;
        return stringRedisTemplate.opsForValue().get(key);
    }

    private boolean verifyLimit(String address) {
        String key = Const.VERIFY_EMAIL_LIMIT + address;
        return flow.limitOnceCheck(key, verifyLimit);
    }

    public Account findAccountByNameOrEmail(String text) {
        return this.query()
                .eq("username", text).or()
                .eq("email", text)
                .one();
    }

    private boolean frequencyCheck(int userId) {
        String key = Const.JWT_FREQUENCY + userId;
        return flow.limitOnceUpgradeCheck(key, limit_frequency, limit_base, limit_upgrade);
    }
}

