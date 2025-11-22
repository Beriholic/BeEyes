package cv.beriholic.beeyes.models.dto;

import cv.beriholic.beeyes.consts.ServerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerStatusDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5053074909422156763L;

    private Integer currentStatus;
    private Long updateTime;

    public static ServerStatusDTO UnknowStatus() {
        return new ServerStatusDTO(ServerStatus.UNKNOW.getKey(), System.currentTimeMillis());
    }

    public static ServerStatusDTO of(ServerStatus status) {
        return new ServerStatusDTO(status.getKey(), System.currentTimeMillis());
    }

    public void updateStatus(ServerStatus status) {
        this.currentStatus = status.getKey();
        this.updateTime = System.currentTimeMillis();
    }

    public boolean isStatusUpdated(ServerStatus status) {
        return this.currentStatus.equals(status.getKey());
    }

    /**
     * 检查是否半分钟（30秒）未更新状态
     *
     * @return true表示超过30秒未更新，false表示在30秒内有更新
     */
    public boolean isNotUpdatedForHalfMinute() {
        return System.currentTimeMillis() - this.updateTime > 30000L;
    }

    /**
     * 检查是否指定秒数未更新状态
     *
     * @param seconds 指定的秒数
     * @return true表示超过指定秒数未更新，false表示在指定秒数内有更新
     */
    public boolean isNotUpdatedForSeconds(long seconds) {
        return System.currentTimeMillis() - this.updateTime > seconds * 1000L;
    }
}
