package xyz.beriholic.beeyes.client.utils;

import com.alibaba.fastjson2.JSON;
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

    public Response doGet(String url) {
        return this.doGet(url, config.getAddress(), config.getToken());
    }

    public Response doGet(String url, String address, String token) {
        try {
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .GET()
                    .uri(new URI(address + "/api/client" + url))
                    .header("Authorization", token)
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return JSONObject.parseObject(response.body(), Response.class);
        } catch (Exception e) {
            log.error("请求(GET)服务端时出现错误", e);
            return Response.onFail(e);
        }
    }

    public Response doPost(String url, Object data) {
        try {
            String raw = JSON.toJSONString(data);
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(raw))
                    .uri(new URI(config.getAddress() + "/api/client" + url))
                    .header("Authorization", config.getToken())
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return JSONObject.parseObject(response.body(), Response.class);
        } catch (Exception e) {
            log.error("请求(POST)服务端时出现错误", e);
            return Response.onFail(e);
        }
    }
}
