package xyz.beriholic.beeyes.client.config;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.beriholic.beeyes.client.api.ClientApi;
import xyz.beriholic.beeyes.client.entity.ConnectionConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Slf4j
@Configuration
public class ServerConfiguration {
    private final String CONFIG_FILE_PATH = "client-config/server.json";

    @Resource
    private ClientApi api;

    @Bean
    ConnectionConfig connectionConfig() {
        log.info("加载服务端连接配置...");
        ConnectionConfig config = this.loadLocalConfig();

        if (config == null) {
            config = registerToServer();
        }
        log.info("连接到服务端: {}", config);
        return config;
    }

    private ConnectionConfig registerToServer() {
        Scanner sc = new Scanner(System.in);
        String address, token;

        do {
            log.info("服务端地址[host:port]: ");
            address = sc.nextLine();
            log.info("服务端token: ");
            token = sc.nextLine();
        } while (!api.registerToServer(address, token));

        ConnectionConfig config = new ConnectionConfig(address, token);
        this.saveConfigToLocal(config);
        return config;
    }

    private ConnectionConfig loadLocalConfig() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) {
            try (FileInputStream stream = new FileInputStream(configFile)) {
                String raw = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                return JSONObject.parseObject(raw).to(ConnectionConfig.class);
            } catch (IOException e) {
                log.error("加载本地配置文件出错", e);
            }
        }
        return null;
    }

    private void saveConfigToLocal(ConnectionConfig config) {
        File dir = new File("client-config");
        if (!dir.exists() && dir.mkdir()) {
            log.info("创建配置文件目录成功");
        }
        File file = new File(CONFIG_FILE_PATH);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(JSONObject.from(config).toJSONString());
        } catch (IOException e) {
            log.error("写入配置文件到本地失败", e);
        }

        log.info("写入本地配置文件成功");
    }
}
