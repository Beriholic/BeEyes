package xyz.beriholic.beeyes.entity.vo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccountPasswordChangeVO {
    @NotBlank private String old;
    @NotBlank private String password;
}
