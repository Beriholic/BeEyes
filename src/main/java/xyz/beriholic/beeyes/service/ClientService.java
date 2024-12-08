package xyz.beriholic.beeyes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.beriholic.beeyes.entity.dto.Client;
import xyz.beriholic.beeyes.entity.vo.request.ClientReportVO;

public interface ClientService extends IService<Client> {
    Client getClientById(int id);

    Client getClientByToken(String token);

    String getRegisterToken();

    boolean verifyAndRegister(String token);

    void reportClientDetail(int clientId, ClientReportVO vo);
}
