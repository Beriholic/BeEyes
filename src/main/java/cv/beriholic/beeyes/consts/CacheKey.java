package cv.beriholic.beeyes.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CacheKey {
    MACHINE_ID_TOKEN("machine_id_token", "缓存客户端机器id"),
    MACHINE_RUNTIME_INFO("machine_runtime_info", "客户端运行信息"),
    USER_SERVER_LIST("user_server_list", "用户作用域");
    private final String key;
    @Getter
    private final String desc;

    public String getKey(String key) {
        return this.key + "::" + key;
    }

    public String getKey(Long key) {
        return this.key + "::" + key;
    }
}
