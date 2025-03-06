package xyz.beriholic.beeyes.controller;


import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.beriholic.beeyes.utils.InfluxDBUtils;

import java.util.List;

@RequestMapping("/api/test")
@RestController
public class TestController {
    @Resource
    InfluxDBUtils influxDBUtils;

    @GetMapping("/moni")
    public List<Long> moni() {
        return influxDBUtils.queryCPUUsageLimited(5);
    }
}
