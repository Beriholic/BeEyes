package xyz.beriholic.beeyes.entity.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RenameClientVO {
    Long id;
    String name;
}
