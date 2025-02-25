package xyz.beriholic.beeyes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import xyz.beriholic.beeyes.entity.dto.Machine;
import xyz.beriholic.beeyes.entity.vo.request.MachineInfoReportVO;
import xyz.beriholic.beeyes.entity.vo.request.RuntimeInfoVO;
import xyz.beriholic.beeyes.entity.vo.response.ClientMetricVO;
import xyz.beriholic.beeyes.entity.vo.response.RuntimeInfoCurrentVO;
import xyz.beriholic.beeyes.entity.vo.response.RuntimeInfoHistoryVO;

import java.util.List;

public interface ClientService extends IService<Machine> {
    Machine getClientById(long id);

    Machine getClientByToken(String token);

    boolean verifyAndRegister(String token);

    void reportClientInfo(long clientId, MachineInfoReportVO vo);

    void reportRuntimeInfo(long clientId, @Valid RuntimeInfoVO vo);

    List<ClientMetricVO> getAllClientMetric();

    RuntimeInfoHistoryVO runtimeInfoHistory(long clientId,int timeline);

    RuntimeInfoCurrentVO runtimeInfoCurrent(long clientId);
}
