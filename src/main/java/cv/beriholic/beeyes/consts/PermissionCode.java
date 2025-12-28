package cv.beriholic.beeyes.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermissionCode {
    CRATE_SERVER((short) 0, "创建机器"),
    UPDATE_SERVER((short) 1, "更新机器"),
    DELETE_SERVER((short) 2, "删除机器"),
    SSH_CONNECT((short) 3, "SSH连接"),
    ALERT_MANAGE((short) 4, "告警管理"),
    ;
    private final short code;
    private final String desc;

    public static PermissionCode getByCode(short code) {
        for (PermissionCode permissionCode : values()) {
            if (permissionCode.getCode() == code) {
                return permissionCode;
            }
        }
        throw new IllegalArgumentException("PermissionCode not found");
    }
}
