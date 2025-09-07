package xyz.beriholic.beeyes.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthorizeVO {
    String username;
    String role;
    String token;
    String avatar;
}
