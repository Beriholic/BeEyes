package xyz.beriholic.beeyes.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.beriholic.beeyes.entity.RestBean;
import xyz.beriholic.beeyes.entity.vo.response.ClientMetricVO;
import xyz.beriholic.beeyes.entity.vo.response.RuntimeInfoCurrentVO;
import xyz.beriholic.beeyes.entity.vo.response.RuntimeInfoHistoryVO;
import xyz.beriholic.beeyes.service.ClientService;

import java.time.Duration;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/metric")
public class MetricController {
    private final Duration interval = Duration.ofSeconds(3);
    @Resource
    ClientService clientService;

    @GetMapping("/list")
    public Flux<ServerSentEvent<List<ClientMetricVO>>> getClientMetricList() {
        return Flux.interval(interval)
                .map(seq -> ServerSentEvent.<List<ClientMetricVO>>builder()
                        .id(String.valueOf(seq))
                        .data(clientService.getAllClientMetric())
                        .build()
                ).concatWith(Mono.just(ServerSentEvent.<List<ClientMetricVO>>builder().event("end").build()));
    }

    @GetMapping("/history")
    public RestBean<RuntimeInfoHistoryVO> runtimeInfoHistory(
            @RequestParam long id,
            @RequestParam @Valid int timeline
    ) {
        if(timeline < 0){
            timeline=1;
        }
        RuntimeInfoHistoryVO vo = clientService.runtimeInfoHistory(id,timeline);
        return RestBean.success(vo);
    }

    @GetMapping("/current")
    public Flux<ServerSentEvent<RuntimeInfoCurrentVO>> runtimeInfoCurrent(
            @RequestParam long id
    ) {
        return Flux.interval(interval)
                .map(seq -> ServerSentEvent.<RuntimeInfoCurrentVO>builder()
                        .id(String.valueOf(seq))
                        .data(clientService.runtimeInfoCurrent(id))
                        .build())
                .concatWith(Mono.just(ServerSentEvent.<RuntimeInfoCurrentVO>builder().event("end").build()));
    }
}
