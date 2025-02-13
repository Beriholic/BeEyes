package xyz.beriholic.beeyes.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.beriholic.beeyes.entity.vo.response.ClientMetricVO;
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
}
