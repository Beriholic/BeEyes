package xyz.beriholic.beeyes.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;


@Data
@TableName("tb_machine")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Machine {
    @TableId
    Long id;
    String name;
    String token;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date registerTime;
    String location;
    String nodeName;
    String active;
}
