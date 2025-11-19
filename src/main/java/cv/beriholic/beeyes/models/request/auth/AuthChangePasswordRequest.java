package cv.beriholic.beeyes.models.request.auth;


import lombok.Data;

@Data
public class AuthChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
