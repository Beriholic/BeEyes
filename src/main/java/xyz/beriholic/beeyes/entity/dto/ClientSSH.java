package xyz.beriholic.beeyes.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@TableName("tb_client_ssh")
@Accessors(chain = true)
public class ClientSSH {
    @TableId
    Long id;
    Integer port;
    String username;
    String password;
}
