package xyz.beriholic.beeyes.entity.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class NewMachineVO {
    private String name;
    private String location;
    private String nodeName;
}
