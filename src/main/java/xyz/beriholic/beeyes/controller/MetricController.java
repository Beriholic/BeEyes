package xyz.beriholic.beeyes.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.beriholic.beeyes.entity.RestBean;
import xyz.beriholic.beeyes.entity.vo.response.ClientMetricVO;
import xyz.beriholic.beeyes.service.ClientService;

import java.util.List;

@RestController
@RequestMapping("/api/metric")
public class MetricController {
    @Resource
    ClientService clientService;

    @GetMapping("/list")
    public RestBean<List<ClientMetricVO>> getClientMetricList() {
        List<ClientMetricVO> metricList = clientService.getAllClientMetric();
        return RestBean.success(metricList);
    }

}
