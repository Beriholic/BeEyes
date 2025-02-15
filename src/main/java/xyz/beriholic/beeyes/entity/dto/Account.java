package xyz.beriholic.beeyes.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import xyz.beriholic.beeyes.entity.BaseData;

import java.util.Date;

@Data
@TableName("tb_account")
@AllArgsConstructor
public class Account implements BaseData {
    @TableId(type = IdType.AUTO)
    Long id;
    String username;
    String password;
    String email;
    String role;
    Date registerTime;
}
