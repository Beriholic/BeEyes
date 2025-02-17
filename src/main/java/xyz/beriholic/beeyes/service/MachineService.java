package xyz.beriholic.beeyes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import xyz.beriholic.beeyes.entity.dto.Machine;
import xyz.beriholic.beeyes.entity.vo.request.MachineNewVO;
import xyz.beriholic.beeyes.entity.vo.request.MachineUpdateVO;
import xyz.beriholic.beeyes.entity.vo.request.RenameClientVO;
import xyz.beriholic.beeyes.entity.vo.request.SSHInfoVO;
import xyz.beriholic.beeyes.entity.vo.response.MachineInfoVO;
import xyz.beriholic.beeyes.entity.vo.response.SSHInfoSaveVO;

public interface MachineService extends IService<Machine> {
    void renameMachine(@Valid RenameClientVO vo);

    String newMachine(@Valid MachineNewVO vo);

    void deleteMachine(Long id);

    void updateMachine(@Valid MachineUpdateVO vo);

    MachineInfoVO machineInfo(long id);

    SSHInfoVO sshInfo(long id);

    void saveSSHInfo(@Valid SSHInfoSaveVO vo);
}
