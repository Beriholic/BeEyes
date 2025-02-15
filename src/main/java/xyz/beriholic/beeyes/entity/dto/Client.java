package xyz.beriholic.beeyes.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;


@Data
@TableName("tb_client")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Client {
    @TableId
    Long id;
    String name;
    String token;
    Date registerTime;
    String location;
    String nodeName;
    String active;
}
