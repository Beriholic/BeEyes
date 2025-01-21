package xyz.beriholic.beeyes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.beriholic.beeyes.entity.dto.Account;
import xyz.beriholic.beeyes.entity.vo.request.ConfirmResetVO;
import xyz.beriholic.beeyes.entity.vo.request.EmailResetVO;
import xyz.beriholic.beeyes.entity.vo.response.AuthorizeVO;

public interface AccountService extends IService<Account> {
    Account findAccountByNameOrEmail(String text);

    String registerEmailVerifyCode(String type, String email, String address);

    String resetEmailAccountPassword(EmailResetVO info);

    String resetConfirm(ConfirmResetVO info);

    AuthorizeVO authenticate(String username, String password);
}
