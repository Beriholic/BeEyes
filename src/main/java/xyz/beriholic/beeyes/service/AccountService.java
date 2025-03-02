package xyz.beriholic.beeyes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.constraints.NotBlank;
import xyz.beriholic.beeyes.entity.dto.Account;
import xyz.beriholic.beeyes.entity.vo.request.ConfirmResetVO;
import xyz.beriholic.beeyes.entity.vo.request.EmailResetVO;
import xyz.beriholic.beeyes.entity.vo.response.AccountVO;
import xyz.beriholic.beeyes.entity.vo.response.AuthorizeVO;

public interface AccountService extends IService<Account> {
    Account findAccountByNameOrEmail(String text);

    AuthorizeVO authenticate(String username, String password);

    AccountVO getAccountInfoById(Long id);

    void changeUserNameById(Long id, String username);

    void changeEmailById(Long id, String email);

    void changePasswordById(Long id, @NotBlank String old, @NotBlank String password);

    void changeAvatar(Long id, String avatar);
}
