package xyz.beriholic.beeyes.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
public class AccountVO {
    Long id;
    String username;
    String email;
    String avatar;
    Date registerTime;
}
