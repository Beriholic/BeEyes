package cv.beriholic.beeyes.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Attribute {
    USER_ID("attr_user_id", "用户ID");
    private final String name;
    private final String key;
}
