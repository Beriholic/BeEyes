package cv.beriholic.beeyes.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessId {
    ReportMachineRuntimeInfo("1", "上报机器运行数据");
    private final String key;
    private final String desc;
}
