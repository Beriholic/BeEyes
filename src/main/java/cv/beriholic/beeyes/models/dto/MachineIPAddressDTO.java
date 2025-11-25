package cv.beriholic.beeyes.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MachineIPAddressDTO {
    private String[] ipv4;
    private String[] ipv6;
}
