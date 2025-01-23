package xyz.beriholic.beeyes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import xyz.beriholic.beeyes.entity.dto.Client;
import xyz.beriholic.beeyes.entity.vo.request.ClientReportVO;
import xyz.beriholic.beeyes.entity.vo.request.RuntimeInfoVO;
import xyz.beriholic.beeyes.entity.vo.response.ClientMetricVO;

import java.util.List;

public interface ClientService extends IService<Client> {
    Client getClientById(int id);

    Client getClientByToken(String token);

    String getRegisterToken();

    boolean verifyAndRegister(String token);

    void reportClientInfo(int clientId, ClientReportVO vo);

    void reportRuntimeInfo(int clientId, @Valid RuntimeInfoVO vo);

    List<ClientMetricVO> getAllClientMetric();
}
