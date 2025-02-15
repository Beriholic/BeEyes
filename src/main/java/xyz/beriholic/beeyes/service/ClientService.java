package xyz.beriholic.beeyes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import xyz.beriholic.beeyes.entity.dto.Machine;
import xyz.beriholic.beeyes.entity.vo.request.MachineInfoVO;
import xyz.beriholic.beeyes.entity.vo.request.RuntimeInfoVO;
import xyz.beriholic.beeyes.entity.vo.response.ClientMetricVO;

import java.util.List;

public interface ClientService extends IService<Machine> {
    Machine getClientById(long id);

    Machine getClientByToken(String token);

    boolean verifyAndRegister(String token);

    void reportClientInfo(long clientId, MachineInfoVO vo);

    void reportRuntimeInfo(long clientId, @Valid RuntimeInfoVO vo);

    List<ClientMetricVO> getAllClientMetric();
}
