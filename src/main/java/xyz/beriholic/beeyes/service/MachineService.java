package xyz.beriholic.beeyes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import xyz.beriholic.beeyes.entity.dto.Machine;
import xyz.beriholic.beeyes.model.request.MachineNewVO;
import xyz.beriholic.beeyes.model.request.MachineUpdateVO;
import xyz.beriholic.beeyes.model.request.RenameClientVO;
import xyz.beriholic.beeyes.model.request.SSHInfoVO;
import xyz.beriholic.beeyes.model.response.MachineActiveVO;
import xyz.beriholic.beeyes.model.response.MachineInfoVO;
import xyz.beriholic.beeyes.model.response.SSHInfoSaveVO;

import java.util.List;

public interface MachineService extends IService<Machine> {
    void renameMachine(@Valid RenameClientVO vo);

    String newMachine(@Valid MachineNewVO vo);

    void deleteMachine(Long id);

    void updateMachine(@Valid MachineUpdateVO vo);

    MachineInfoVO machineInfo(long id);

    SSHInfoVO sshInfo(long id);

    void saveSSHInfo(@Valid SSHInfoSaveVO vo);

    List<MachineActiveVO> listActiveMachine();
}
