package xyz.beriholic.beeyes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import xyz.beriholic.beeyes.entity.dto.Machine;
import xyz.beriholic.beeyes.model.request.ReportMachineInfoRequest;
import xyz.beriholic.beeyes.model.request.ReportRuntimeInfoRequest;
import xyz.beriholic.beeyes.model.response.ClientMetricVO;
import xyz.beriholic.beeyes.model.response.RuntimeInfoCurrentVO;
import xyz.beriholic.beeyes.model.response.RuntimeInfoHistoryVO;

import java.util.List;

public interface ClientService extends IService<Machine> {
    Machine getClientById(long id);

    Machine getClientByToken(String token);

    boolean verifyAndRegister(String token);

    void reportClientInfo(long clientId, ReportMachineInfoRequest vo);

    void reportRuntimeInfo(long clientId, @Valid ReportRuntimeInfoRequest vo);

    List<ClientMetricVO> getAllClientMetric();

    RuntimeInfoHistoryVO runtimeInfoHistory(long clientId, int timeline);

    RuntimeInfoCurrentVO runtimeInfoCurrent(long clientId);
}
