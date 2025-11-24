package cv.beriholic.beeyes.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CacheKey {
    MACHINE_ID_TOKEN("machine_id_token", "缓存客户端机器id"),
    USER_SERVER_LIST("user_server_list", "用户作用域"),
    MACHINE_STATUS("machine_status", "机器状态");

    private final String key;
    @Getter
    private final String desc;

    public static String machineIdToken(String token) {
        return MACHINE_ID_TOKEN.key + "::" + token;
    }

    public static String userServerList(Long id) {
        return USER_SERVER_LIST.key + "::" + id;
    }

    public static String machineStatus(Long id) {
        return MACHINE_STATUS.key + "::" + id;
    }

    public static String pageKey(Integer pageIndex, Integer pageSize) {
        return pageIndex + "-" + pageSize;
    }
}
