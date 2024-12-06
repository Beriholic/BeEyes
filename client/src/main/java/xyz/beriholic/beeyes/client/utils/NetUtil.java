package xyz.beriholic.beeyes.client.utils;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import xyz.beriholic.beeyes.client.entity.ConnectionConfig;
import xyz.beriholic.beeyes.client.entity.Response;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Component
public class NetUtil {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Lazy
    @Resource
    private ConnectionConfig config;

    public boolean registerToServer(String address, String token) {
        log.info("注册到服务端...");
        log.debug("Address: {} Token: {}", address, token);

        Response response = this.doGet("/register", address, token);
        if (response.isOk()) {
            log.info("客户端注册成功");
        } else {
            log.error("客户端注册失败: {}", response.msg());
        }

        return response.isOk();
    }

    private Response doGet(String url) {
        return this.doGet(url, config.getAddress(), config.getToken());
    }

    private Response doGet(String url, String address, String token) {
        try {
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .GET()
                    .uri(new URI(address + "/client" + url))
                    .header("Authorization", token)
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return JSONObject.parseObject(response.body(), Response.class);
        } catch (Exception e) {
            log.error("请求服务端注册时出现错误", e);
            return Response.onFail(e);
        }
    }
}
