package xyz.beriholic.beeyes.model.response;

import lombok.Data;

@Data
public class SSHInfoSaveVO {
    long id;
    String username;
    String password;
    int port;
}
