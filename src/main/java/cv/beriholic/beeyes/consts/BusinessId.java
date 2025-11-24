package cv.beriholic.beeyes.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessId {
    ReportMachineRuntimeInfo("1", "上报机器运行数据"),
    ServerStatusUpdated("2", "服务器状态更新");

    private final String key;
    private final String desc;

    public String buildKey(Long identity) {
        return this.key + "_" + identity;
    }
}
