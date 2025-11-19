package cv.beriholic.beeyes.models.request.auth;

import lombok.Data;

@Data
public class AuthLoginRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
}
