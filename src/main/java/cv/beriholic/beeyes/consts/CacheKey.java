package cv.beriholic.beeyes.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CacheKey {
    MACHINE_ID_TOKEN("machine_id_token", "缓存客户端机器id");
    private final String key;
    @Getter
    private final String desc;

    public String getKey(String key) {
        return this.key + "::" + key;
    }
}
