package cv.beriholic.beeyes.service.impl;

import cv.beriholic.beeyes.consts.CacheKey;
import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.entity.dto.MachineView;
import cv.beriholic.beeyes.repository.ServersRepository;
import cv.beriholic.beeyes.repository.UserServiceRepository;
import cv.beriholic.beeyes.service.MachineService;
import cv.beriholic.beeyes.utils.JsonUtil;
import cv.beriholic.beeyes.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {
    private final UserServiceRepository userServiceRepository;
    private final ServersRepository serversRepository;
    private final RedisUtils redisUtils;

    public PageDTO<List<MachineView>> getMachineListByUserId(PageDTO<Long> userIdPage) {
        List<MachineView> machineList;
        String userServerListJson = redisUtils.get(CacheKey.USER_SERVER_LIST.getKey(userIdPage.getData()));
        if (StringUtils.isEmpty(userServerListJson)) {
            List<Long> serverIds = userServiceRepository.getServerIdsByUserId(userIdPage.getData());
            machineList = serversRepository.findByIds(serverIds, MachineView.class);
            redisUtils.set(CacheKey.USER_SERVER_LIST.getKey(userIdPage.getData()), JsonUtil.toJSONString(machineList), 12, TimeUnit.HOURS);
        } else {
            machineList = JsonUtil.parseList(userServerListJson, MachineView.class);
        }
        return PageDTO.paginate(machineList, userIdPage.getPageIndex(), userIdPage.getPageSize());
    }
}
