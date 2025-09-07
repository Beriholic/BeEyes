package xyz.beriholic.beeyes.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MachineActiveVO {
    Long id;
    String name;
    String location;
}
