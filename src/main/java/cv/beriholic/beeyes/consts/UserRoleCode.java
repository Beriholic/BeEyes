package cv.beriholic.beeyes.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRoleCode {
    SUPER_ADMIN((short) 1, "超级管理员"),
    ADMIN((short) 2, "管理员"),
    USER((short) 3, "普通用户");
    private final short code;
    private final String desc;

    public static UserRoleCode getByCode(short code) {
        for (UserRoleCode userRoleCode : UserRoleCode.values()) {
            if (userRoleCode.getCode() == code) {
                return userRoleCode;
            }
        }
        throw new IllegalArgumentException("UserRoleCode not found");
    }

    public static boolean isValid(short roleCode) {
        for (UserRoleCode userRoleCode : UserRoleCode.values()) {
            if (userRoleCode.getCode() == roleCode) {
                return true;
            }
        }
        return false;
    }
}
