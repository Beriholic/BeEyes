package xyz.beriholic.beeyes.model;

import lombok.Data;
import lombok.experimental.Accessors;
import xyz.beriholic.beeyes.entity.dto.Machine;
import xyz.beriholic.beeyes.entity.dto.UserSession;

@Data
@Accessors(chain = true)
public class Context {
    Machine machine;
    UserSession userSession;
}
