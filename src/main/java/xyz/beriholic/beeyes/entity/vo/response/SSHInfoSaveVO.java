package xyz.beriholic.beeyes.entity.vo.response;

import lombok.Data;

@Data
public class SSHInfoSaveVO {
    long id;
    String username;
    String password;
    int port;
}
