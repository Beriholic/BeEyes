package xyz.beriholic.beeyes.entity.vo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MachineUpdateVO {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String location;
    private String nodeName;
}
