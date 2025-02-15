package xyz.beriholic.beeyes.entity.dto;

import cn.dev33.satoken.stp.StpUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import static xyz.beriholic.beeyes.utils.Const.USER_SESSION;

@Data
@Accessors(chain = true)
public class UserSession {
    private Long id;
    private String username;
    private String role;

    public static UserSession get() {
        return (UserSession) StpUtil.getSession().get(USER_SESSION);
    }

    public void save() {
        StpUtil.getSession().set(USER_SESSION, this);
    }
}
