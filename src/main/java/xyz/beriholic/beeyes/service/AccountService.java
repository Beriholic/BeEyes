package xyz.beriholic.beeyes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.userdetails.UserDetailsService;
import xyz.beriholic.beeyes.entity.dto.Account;
import xyz.beriholic.beeyes.entity.vo.request.ConfirmResetVO;
import xyz.beriholic.beeyes.entity.vo.request.EmailResetVO;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);

    String registerEmailVerifyCode(String type, String email, String address);

    String resetEmailAccountPassword(EmailResetVO info);

    String resetConfirm(ConfirmResetVO info);
}
