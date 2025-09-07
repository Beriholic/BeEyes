package xyz.beriholic.beeyes.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RenameClientVO {
    Long id;
    @NotBlank
    String name;
}
